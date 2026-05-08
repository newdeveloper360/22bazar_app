package com.userplay.bazar22.ui.fragments.login

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSetNewPinBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.viewmodels.LoginViewModel
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.hideKeyboard
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetNewPinFragment : Fragment(R.layout.fragment_set_new_pin), View.OnClickListener {
    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: FragmentSetNewPinBinding
    private val args: SetNewPinFragmentArgs by navArgs()
    private val mLoginViewModel: LoginViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 60000 // 1 minute in milliseconds
    private var isCountDownRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentSetNewPinBinding.inflate(inflater, container, false)
            initView()
            observer()
        }
        return mBinding.root
    }

    private fun initView() {
        mBinding.setPin.setOnClickListener(this@SetNewPinFragment)
        mBinding.tvResend.setOnClickListener(this@SetNewPinFragment)
        mBinding.tvResend.isEnabled = false
        startCountDown()
    }

    private fun observer() {
        activity?.let {
            mLoginViewModel.mVerifyForGotOtpResponse.observe(it) { response ->

                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        response.data?.let { data ->
                            if (data.error != null) {
                                if (data.error) {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", data.message.toString())
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                } else {
                                    mPref.setmPillers(0)
                                    it.showToast(data.message.toString())
                                    findNavController().popBackStack(R.id.loginFragment, false)
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("newpin_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("newpin_loading", "loading---->>>>")
                    }
                }
            }


            mLoginViewModel.mForgotOtpResponse.observe(it) { response ->

                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        response.data?.let { data ->
                            if (data.error != null) {
                                if (data.error) {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", data.message.toString())
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                } else {
                                    it.showToast("Otp Send Successfully")
                                    startCountDown()
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("sendOtp_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.hideKeyboard()
                        it.showProgressDialog()

                        Log.e("sendOtp_loading", "loading---->>>>")
                    }
                }
            }

        }
    }

    private fun startCountDown() {
        // Cancel the current timer if it exists
        countDownTimer?.cancel()
        // Reset time left to 0
        timeLeftInMillis = 0

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTextView()
            }

            override fun onFinish() {
                isCountDownRunning = false
                updateTextView()
                mBinding.tvResend.isEnabled = true
            }
        }

        isCountDownRunning = true
        mBinding.tvResend.isEnabled = false
        countDownTimer?.start()
    }

    private fun updateTextView() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val text = if (isCountDownRunning) {
            "Resend OTP in ${String.format("%02d:%02d", minutes, seconds)}"
        } else {
            getString(R.string.resend_otp)

        }
        mBinding.tvResend.text = text
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.setPin -> {
                        if (edOtp.text.toString().trim().isEmpty()) {
                            edOtp.error = "OTP can't be empty"
                            return@let
                        }
                        if (edOtp.text.toString().trim().length != 4) {
                            edOtp.error = "Invalid Otp"
                            return@let
                        }

                        if (edMpin.text.toString().trim().isEmpty()) {
                            edMpin.error = "Mpin can't be empty"
                            return@let
                        }

                        if (edMpin.text.toString().trim().length != 4) {
                            edMpin.error = "Mpin should be 4 digits"
                            return@let
                        } else {
                            if (CheckNetwork.isNetworkConnected) {
                                mLoginViewModel.verifyForgotOtp(
                                    args.phone,
                                    edOtp.text.toString().trim(),
                                    edMpin.text.toString().trim()
                                )
                            } else {
                                val dialog = InternetErrorDialogFragment()
                                dialog.show(childFragmentManager, "internet")
                            }
                        }
                    }

                    R.id.tvResend -> {
                        if (CheckNetwork.isNetworkConnected) {
                            mLoginViewModel.forgotOtp(args.phone)
                        } else {
                            val dialog = InternetErrorDialogFragment()
                            dialog.show(childFragmentManager, "internet")
                        }
                    }
                }
            }
        }
    }
}