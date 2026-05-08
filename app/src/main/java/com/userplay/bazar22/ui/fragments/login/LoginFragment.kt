package com.userplay.bazar22.ui.fragments.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentLoginBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork.Companion.isNetworkConnected
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.MainActivity
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.viewmodels.LoginViewModel
import com.userplay.bazar22.utils.BiometricAuthListener
import com.userplay.bazar22.utils.BiometricUtils
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.FCM_KEY
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.hideKeyboard
import com.userplay.bazar22.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login), View.OnClickListener,
    BiometricAuthListener {

    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: FragmentLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels()

    var mActivity: FragmentActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentLoginBinding.inflate(inflater, container, false)
            mActivity = activity
            initView()
            observer()

        }
        return mBinding.root
    }


    private fun initView() {
        mBinding.apply {
            if (mPref.getMatkaEnable()) {
                /* val action =
                     LoginFragmentDirections.actionLoginFragmentToMatkaFragment()
                 findNavController().navigate(action)*/
                activity?.let {
                    startActivity(
                        Intent(
                            it,
                            MainActivity::class.java
                        )
                    )
                    it.finish()
                }

            } else {
                if (( Constants.sessionExpiredDialog || mPref.getIsUserLoginWithPin(Constants.IS_USER_LOGIN_WITH_MPIN)) && mPref.getIsUserLogin(
                        Constants.IS_USER_LOGIN
                    )
                ) {
                    edMobileNo.setText(mPref.getPhone(Constants.PHONE))
                    imgThumb.visibility = View.VISIBLE
                    edMobileNo.visibility = View.GONE
                    tvMobileTitle.visibility = View.GONE
                    signup.visibility = View.GONE
                }
                imgThumb.setOnClickListener {
                    if (BiometricUtils.isBiometricReady(mActivity!!)) {
                        BiometricUtils.showBiometricPrompt(
                            activity = mActivity!!,
                            listener = this@LoginFragment,
                            cryptoObject = null,
                        )
                    } else {
                        Toast.makeText(
                            mActivity!!,
                            "No biometric feature perform on this device",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                forgetPassword.setOnClickListener(this@LoginFragment)
                login.setOnClickListener(this@LoginFragment)
                signup.setOnClickListener(this@LoginFragment)
                edMpin.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (mPref.getIsUserLoginWithPin(Constants.IS_USER_LOGIN_WITH_MPIN) && mPref.getIsUserLoginWithPin(
                                Constants.IS_USER_LOGIN
                            ) && mPref.getmPillers(Constants.PILLERS) != 0
                        ) {
                            if (edMpin.text.toString().trim().length == 4) {
                                if (mPref.getmPillers(Constants.PILLERS) == edMpin.text.toString()
                                        .trim().toInt()
                                ) {
                                    startActivity(
                                        Intent(
                                            mActivity!!,
                                            MainActivity::class.java
                                        )
                                    )
                                    mActivity!!.finish()
                                }
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })
            }

        }
    }

    private fun observer() {
        activity?.let {
            mLoginViewModel.mLoginResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        mPref.setSessionOutStatus(isSessionOut = false)
                        it.dismissDialog()
                        response.data.let { data ->
                            if (data?.error != null) {
                                if (data.error) {
                                    val bundle = Bundle()
                                    val dialog = ErrorDialogFragment()
                                    bundle.putString("message", data.message.toString())
                                    dialog.arguments = bundle
                                    dialog.show(childFragmentManager, "error")
                                } else {
                                    mPref.setToken(data.response?.token)
                                    if (data.response?.user?.confirmed == 0) {
                                        data.response.user?.phone?.let { phone ->
                                            val action =
                                                LoginFragmentDirections.actionLoginFragmentToSendOtpFragment(
                                                    phone
                                                )
                                            findNavController().navigate(action)
                                        } ?: kotlin.run {
                                            val action =
                                                LoginFragmentDirections.actionLoginFragmentToSendOtpFragment(
                                                    ""
                                                )
                                            findNavController().navigate(action)
                                        }

                                    } else {
                                        data.response?.user.let { user ->
                                            if (user != null) {
                                                if (user.phone == "1234567890") {
                                                    mPref.setMatkaEnable(true)
                                                    /* val action =
                                                         LoginFragmentDirections.actionLoginFragmentToMatkaFragment()
                                                     findNavController().navigate(action)*/
                                                    startActivity(
                                                        Intent(
                                                            it,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                    it.finish()
                                                } else {
                                                    mPref.apply {
                                                        setID(user.id)
                                                        setName(user.name)
                                                        setmPillers(
                                                            mBinding.edMpin.text.toString().trim()
                                                                .toInt()
                                                        )
                                                        setPhone(user.phone)
                                                        setBalance(user.balance)
                                                        setGeneralNotification(user.generalNoti)
                                                        setStartLineNotification(user.startlineNoti)
                                                        setDesawarNotification(user.desawarNoti)
                                                        setFcmKey(user.fcm)
                                                        setOwnCode(user.ownCode)
                                                        setBonus(user.bonus)
                                                        setBlocked(user.blocked)
                                                        setRole(user.role)
                                                        setConfirmed(user.confirmed)
                                                        setIsUserLogin(true)
                                                        setUserID(user.withdrawDetails?.userId)
                                                        setAccountHolderName(user.withdrawDetails?.accountHolderName)
                                                        setUserUpi(user.withdrawDetails?.upiId)
                                                        setAccountNumber(user.withdrawDetails?.accountNumber.toString())
                                                        setIFSCode(user.withdrawDetails?.accountIfscCode)
                                                    }

                                                    startActivity(
                                                        Intent(
                                                            it,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                    it.finish()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("login_error", "" + response.message)

                    }

                    is ApiState.Loading -> {
                        it.hideKeyboard()
                        it.showProgressDialog()
                        Log.e("login_loading", "loading---->>>>")
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Please Allow Notification Permission!",
                        Toast.LENGTH_SHORT
                    ).show()
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS
                        ), 451
                    )
                    return@let
                }

                when (v?.id) {

                    R.id.signup -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
                        findNavController().navigate(action)
                    }

                    R.id.forget_password -> {
                        if (mPref.getOtpSysmteRemoved() == 1) {
                            val url =
                                "https://api.whatsapp.com/send?phone=${
                                    mPref.getSupportNumber(
                                        Constants.SUPPORT_NUMBER
                                    )
                                }"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } else {
                            val action =
                                LoginFragmentDirections.actionLoginFragmentToForgotDialogFragment()
                            findNavController().navigate(action)
                        }
                    }

                    R.id.login -> {
                        if (edMobileNo.text.toString().trim().isEmpty()) {
                            edMobileNo.error = "Phone number can't be empty"
                            return@let
                        }
                        if (edMobileNo.text.toString()
                                .trim().length > 12 || edMobileNo.text.toString().trim().length < 10
                        ) {
                            edMobileNo.error = "Phone number invalid"
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
                            if (isNetworkConnected) {
                                if (mPref.getIsUserLoginWithPin(Constants.IS_USER_LOGIN_WITH_MPIN) && mPref.getIsUserLoginWithPin(
                                        Constants.IS_USER_LOGIN
                                    ) && mPref.getmPillers(Constants.PILLERS) != 0
                                ) {
                                    if (mPref.getmPillers(Constants.PILLERS) == edMpin.text.toString()
                                            .trim().toInt()
                                    ) {
                                        startActivity(
                                            Intent(
                                                mActivity!!,
                                                MainActivity::class.java
                                            )
                                        )
                                        mActivity!!.finish()
                                    } else {
                                        edMpin.error = "Invalid Mpin"
                                    }
                                } else {
                                    mLoginViewModel.getLogin(
                                        edMobileNo.text.toString().trim(),
                                        edMpin.text.toString().trim().toInt(),
                                        mPref.getFcmKey(FCM_KEY)
                                    )
                                }
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

    override fun onBiometricAuthenticateError(error: Int, errMsg: String) {
        when (error) {
            BiometricPrompt.ERROR_USER_CANCELED -> {

            }

            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {

            }
        }
    }

    override fun onBiometricAuthenticateSuccess(result: BiometricPrompt.AuthenticationResult) {
        if (isNetworkConnected) {
            mActivity!!.startActivity(
                Intent(
                    mActivity!!,
                    MainActivity::class.java
                )
            )
            mActivity!!.finish()
        } else {
            val dialog = InternetErrorDialogFragment()
            dialog.show(childFragmentManager, "internet")
        }
    }
}