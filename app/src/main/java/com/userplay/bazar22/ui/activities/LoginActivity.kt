package com.userplay.bazar22.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.login_container) as NavHostFragment
        mNavController = navHostFragment.findNavController()
        setSupportActionBar(mBinding.lytToolbar.toolBar)
        setupActionBarWithNavController(mNavController)

    }


    override fun onSupportNavigateUp(): Boolean {
        return mNavController.navigateUp(mAppBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when (v?.id) {  

        }
    }
}