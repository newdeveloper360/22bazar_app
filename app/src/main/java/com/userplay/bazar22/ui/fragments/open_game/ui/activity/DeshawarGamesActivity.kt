package com.userplay.bazar22.ui.fragments.open_game.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.userplay.bazar22.databinding.ActivityDeshawarGamesBinding
import com.userplay.bazar22.models.get_markets.Market
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.callbacks.OnGameListener
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.fragments.home.adapters.MarketAdapter
import com.userplay.bazar22.ui.fragments.open_game.ui.fragment.DeshawarGamesClosedFragment
import com.userplay.bazar22.ui.fragments.open_game.viewmodels.OpenGameViewModel
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeshawarGamesActivity : AppCompatActivity(), OnGameListener {

    @Inject
    lateinit var mPref: MatkaPref

    private lateinit var mBinding: ActivityDeshawarGamesBinding
    private val mOpenGameViewModel: OpenGameViewModel by viewModels()
    private val mSharedViewModels: SharedViewModels by viewModels()
    private var mMarketsList: ArrayList<Market> = ArrayList()
    private val mMarketAdapter by lazy { MarketAdapter(mMarketsList, this,0,"desawar") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDeshawarGamesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
        observer()
    }

    private fun initView() {
        mBinding.rvGames.apply {
            layoutManager =
                LinearLayoutManager(this@DeshawarGamesActivity, RecyclerView.VERTICAL, false)
            adapter = mMarketAdapter
        }

        mBinding.back.setOnClickListener {
            finish()
        }

        if (CheckNetwork.isNetworkConnected) {
            mOpenGameViewModel.getDesawarMarket()
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(supportFragmentManager, "internet")
            // showToast(resources.getString(R.string.check_your_internet))
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {
        mOpenGameViewModel.mMarketResponse.observe(this) { response ->
            when (response) {
                is ApiState.Success -> {
                    dismissDialog()

                    response.data?.let { data ->
                        if (data.error != null) {
                            if (data.error) {
                                showToast(data.message.toString())
                            } else {
                                if (data.response?.markets != null) {
                                    mMarketsList.clear()
                                    mMarketsList.addAll(data.response.markets)
                                    mMarketAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {
                    dismissDialog()
                    Log.e("market_error", "" + response.message)
                }

                is ApiState.Loading -> {
                    showProgressDialog()
                    Log.e("market_loading", "loading---->>>>")
                }
            }
        }

        mSharedViewModels.mBalance?.observe(this) {
            mBinding.tvAmount.text = it
        }
    }


    override fun onResume() {
        super.onResume()
        mBinding.tvAmount.text = mPref.getBalance(Constants.BALANCE).toString()
    }

    override fun onGameClosedClick(
        position: Int?,
        openTime: String?,
        closeTime: String?,
        openResultTime: String?,
        closeResultTime: String?,
        bidName: String?
    ) {
        Log.e(
            "close time",
            "" + openTime + " " + closeTime + " " + openResultTime + " " + closeResultTime
        )

        val dialog = DeshawarGamesClosedFragment()
        val bundle = Bundle()
        bundle.putString("openTime", openTime)
        bundle.putString("closeTime", closeTime)
        bundle.putString("openResultTime", openResultTime)
        bundle.putString("closeResultTime", closeResultTime)
        bundle.putString("bidName", bidName)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "OpenGame")

    }

    override fun onGameStartClick(
        position: Int,
        marketID: Int,
        mGameName: String,
        openStatus: Boolean
    ) {
        startActivity(
            Intent(this, OpenGameActivity::class.java).putExtra("marketID", marketID)
                .putExtra("gameName", mGameName)
        )
    }
}