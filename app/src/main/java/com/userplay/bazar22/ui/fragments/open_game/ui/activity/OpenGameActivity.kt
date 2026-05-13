package com.userplay.bazar22.ui.fragments.open_game.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.ActivityOpenGameBinding
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OpenGameActivity : AppCompatActivity(), View.OnClickListener,NavController.OnDestinationChangedListener {

    @Inject
    lateinit var mPref : MatkaPref
    private lateinit var mBinding: ActivityOpenGameBinding
    private lateinit var mNavController: NavController
    private val mSharedViewModels : SharedViewModels by viewModels()
    var mMarketID = 0
    var mGameName = ""
    private var mBundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOpenGameBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
        observer()
        if (intent != null)
        {
            mMarketID = intent.getIntExtra("marketID",0)
            mGameName = intent.getStringExtra("gameName").toString()
            mBundle.putInt("marketID",mMarketID)
        }
    }

    private fun observer() {
        mSharedViewModels.mBalance?.observe(this) {
            mBinding.tvAmount.text = it
        }
    }

    private fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_container) as NavHostFragment
        mNavController = navHostFragment.findNavController()

        mBinding.back.setOnClickListener {
            finish()
        }

        mBinding.apply {
            jantari.setOnClickListener(this@OpenGameActivity)
            crossing.setOnClickListener(this@OpenGameActivity)
            noToNo.setOnClickListener(this@OpenGameActivity)
            mNavController.addOnDestinationChangedListener(this@OpenGameActivity)
        }
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            when (v?.id) {
                R.id.jantari -> {
                    mPref.setGameSubName("Jantri")
                    mNavController.navigateUp() // to clear previous navigation history
                    mNavController.navigate(R.id.jantriFragment,mBundle)
                }

                R.id.crossing -> {
                    mPref.setGameSubName("Crossing Number")
                    mNavController.navigateUp() // to clear previous navigation history
                    mNavController.navigate(R.id.crossingNumberFragment,mBundle)
                }

                R.id.no_to_no -> {
                    mPref.setGameSubName("No To No")
                    mNavController.navigateUp() // to clear previous navigation history
                    mNavController.navigate(R.id.noToNoFragment,mBundle)
                }
            }
        }
    }

    @SuppressLint("AppCompatMethod")
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(destination.id)
        {

            R.id.jantriFragment ->{
                mBinding.headerText.text ="Jantri"
            }

            R.id.crossingNumberFragment ->{
                mBinding.headerText.text ="Crossing Number"
            }
            R.id.noToNoFragment ->{
                mBinding.headerText.text ="No To No"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
    }

}