package com.userplay.bazar22.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.activity.viewModels
import com.userplay.bazar22.BuildConfig
import com.userplay.bazar22.databinding.ActivitySplashBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork.Companion.isNetworkConnected
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.printer.BillPrintActivity.Companion.startPrintActivity
import com.userplay.bazar22.ui.dialogs.BlockDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.UpdateDialogeFragment
import com.userplay.bazar22.ui.viewmodels.SharedViewModels
import com.userplay.bazar22.ui.viewmodels.SplashViewModel
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.IS_USER_LOGIN
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import io.branch.referral.Branch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: ActivitySplashBinding
    private val mSplashViewModel: SplashViewModel by viewModels()
    private val mSharedViewModels: SharedViewModels by viewModels()

    override fun onStart() {
        super.onStart()

        val intentData = intent?.data
        // Initialize Branch SDK
        Branch.sessionBuilder(this).withCallback { referringParams, error ->
            if (error == null) {
                // Successfully fetched referral data
                val agentCode = referringParams?.getString("agentCode")
                if (!agentCode.isNullOrEmpty()) {
                    mPref.setAgentCode(agentCode)
                    Toast.makeText(this, "CODE: $agentCode", Toast.LENGTH_LONG).show()
                }
            } else {
                // Error fetching referral data
                Log.e("BranchError", "Branch SDK Error: ${error.message}")
            }
        }.withData(this.intent?.data).init()

        // Handle custom scheme links
        intentData?.let { uri ->
            if (uri.scheme == "bazar22" && uri.host == "open") {
                val agentCode = uri.getQueryParameter("agentCode")
                if (!agentCode.isNullOrEmpty()) {
                    Toast.makeText(this, "Agent Code: $agentCode", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        //startPrintActivity()
        initView()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (Constants.PRODUCTION_APP)
            mBinding.llDevelopedBy.visibility = GONE

        if (isNetworkConnected) {
            mBinding.tvVersion.text = "Version : " + BuildConfig.VERSION_CODE
            mSplashViewModel.getAppData()
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(supportFragmentManager, "internet")
        }
    }

    private fun observer() {
        mSplashViewModel.mGetAppDataResponse.observe(this) { response ->

            when (response) {
                is ApiState.Success -> {
                    if (response.data?.error != null) {
                        if (response.data.error) {
                            showToast(response.data.message.toString())
                        } else {
                            val isSessionOut=if(!Constants.sessionExpiredDialog){
                                false
                            }else if(Constants.sessionExpiredDialog && mPref.getSessionOutStatus()){
                                true
                            }
                            else{
                                false
                            }
                            if (response.data.response?.appData?.version != null) {
                                response.data.response.appData.let { data ->
                                    data.let {
                                        mPref.setUserLevelSystem(it.userLevelSystem)
                                        mPref.setOtpSysmteRemoved(it.otpSystemRemoved)
                                        mPref.setHomeMessage(it.homeMessage)
                                        mPref.setSupportNumber(it.supportNumber)
                                        mPref.setAppUpdateLink(it.appUpdateLink)
                                        mPref.setSupportTime(it.supportTime)
                                        mPref.setWithDrawCondition(it.withdrawalCondition)
                                        mPref.setRuleNotice(it.rulesNotice)
                                        mPref.setMinWithdraw(it.minWithdraw)
                                        mPref.setMinBid(it.minBidAmount)
                                        mPref.setMinDeposit(it.minDeposit)
                                        mPref.setInviteBonus(it.inviteBonus)
                                        mPref.setInviteSystemEnable(it.inviteSystemEnable)
                                        if (it.inviteSystemEnable != null && it.inviteSystemEnable == 1) {
                                            mPref.setEarningSystem(true)
                                        } else {
                                            mPref.setEarningSystem(false)
                                        }
                                        mPref.setWelcomeBonus(it.welcomeBonus)
                                        mPref.setAdminUpi(it.adminUpi)
                                        mPref.setTelegramEnable(it.telegramEnable)
                                        mPref.setTelegramLink(it.telegramLink)
                                        mPref.setWhatsAppEnable(it.whatsappEnable)
                                        mPref.setWhatsAppNumber(it.whatsappNumber)
                                        mPref.setWithDrawOpenTime(it.withdrawOpenTime)
                                        mPref.setWithDrawCloseTime(it.withdrawCloseTime)
                                        mPref.setPaymentMethod(it.paymentMethod)
                                        mPref.setAutoResultApi(it.autoResultApi.toString())
                                        mPref.setSmsAPiKey(it.smsApiKey)
                                        mPref.setBankWithDrawEnable(it.bankWithdrawEnable)
                                        mPref.setUpiWithDrawEnable(it.upiWithdrawEnable)
                                        mPref.setChartsUrl(it.chartsUrl)
                                        mPref.setHomePageImageUrl(it.homepageImageUrl)
                                        mPref.setVersion(it.version)
                                        mPref.setEnableDesawar(it.enableDesawar)
                                        mPref.setEnableDesawarOlny(it.enableDesawarOnly)
                                        mPref.setShowResultsOnly(it.showResultsOnly)
                                        mPref.setMainTainMode(it.maintainMode)
                                        mPref.setPlayStoreEnable(it.playStoreEnable)
                                        mPref.setSliderUrl(it.sliderUrl)

                                        if (mPref.getIsUserLogin(IS_USER_LOGIN)) {
                                            mPref.setBalance(response.data.response.user?.balance)
                                            mPref.setBlocked(response.data.response.user?.blocked)
                                        }
                                    }
                                }

                                if (mPref.getMainTainMode(Constants.MAINTAIN_MODE) == 1) {
                                    val bundle = Bundle()
                                    val dialog = BlockDialogFragment()
                                    bundle.putString(
                                        "message",
                                        "We are under maintenance. Please try after some time."
                                    )
                                    bundle.putString(
                                        "type",
                                        "maintain"
                                    )
                                    dialog.arguments = bundle
                                    dialog.show(supportFragmentManager, "error")
                                } else if (response.data.response.appData.version > BuildConfig.VERSION_CODE) {
                                    val dialog = UpdateDialogeFragment()
                                    dialog.show(supportFragmentManager, "update")
                                } else {
                                    response.data.response.appData.let { data ->
                                        data.let {
                                            if (mPref.getIsUserLogin(IS_USER_LOGIN) && !mPref.getIsUserLoginWithPin(
                                                    Constants.IS_USER_LOGIN_WITH_MPIN
                                                ) && !isSessionOut
                                            ) {
                                                if (mPref.getBlocked(Constants.BLOCKED) == 1) {
                                                    val bundle = Bundle()
                                                    val dialog = BlockDialogFragment()
                                                    bundle.putString(
                                                        "message",
                                                        "Your Account is Blocked due to suspicious Activity"
                                                    )
                                                    bundle.putString(
                                                        "type",
                                                        "block"
                                                    )
                                                    dialog.arguments = bundle
                                                    dialog.show(supportFragmentManager, "error")
                                                } else {
                                                    startActivity(
                                                        Intent(
                                                            this@SplashActivity,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                            } else {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    delay(2000)
                                                    startActivity(
                                                        Intent(
                                                            this@SplashActivity,
                                                            LoginActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {
                    showToast("Server Error : ${response.message}")
                    Log.e("app_data_error", "" + response.message)
                }

                is ApiState.Loading -> {
                    Log.e("app_data_loading", "Loading...>>>>>")
                }

            }
        }

        mSharedViewModels.isCancel?.observe(this) {

            if (it) {
                if (mPref.getMainTainMode(Constants.MAINTAIN_MODE) == 1) {
                    val bundle = Bundle()
                    val dialog = BlockDialogFragment()
                    bundle.putString(
                        "message",
                        "We are under maintenance. Please try after some time."
                    )
                    bundle.putString(
                        "type",
                        "maintain"
                    )
                    dialog.arguments = bundle
                    dialog.show(supportFragmentManager, "error")
                } else if (mPref.getIsUserLogin(IS_USER_LOGIN)) {
                    if (mPref.getBlocked(Constants.BLOCKED) == 1) {
                        val bundle = Bundle()
                        val dialog = BlockDialogFragment()
                        bundle.putString(
                            "message",
                            "Your Account is Blocked due to suspicious Activity"
                        )
                        bundle.putString(
                            "type",
                            "block"
                        )
                        dialog.arguments = bundle
                        dialog.show(supportFragmentManager, "error")
                    } else {
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                LoginActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }
    }
}