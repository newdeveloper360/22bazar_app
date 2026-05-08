package com.userplay.bazar22.ui.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSendOtpBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork.Companion.isNetworkConnected
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.MainActivity
import com.userplay.bazar22.ui.fragments.login.SetNewPinFragmentArgs
import com.userplay.bazar22.ui.viewmodels.LoginViewModel
import com.userplay.bazar22.utils.*
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SendOtpFragment : DialogFragment(R.layout.fragment_send_otp), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private var _binding: FragmentSendOtpBinding? = null
    private val mBinding get() = _binding!!
    private val mLoginViewModel: LoginViewModel by viewModels()
    private val args: SetNewPinFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendOtpBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            val window: Window = dialog?.window!!
            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
            val attributes = window.attributes
            attributes.y = 80
            dialog?.window!!.attributes = attributes
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(args.phone!=null && args.phone!=""){
            mBinding.edMobile.setText(args.phone)
            if (isNetworkConnected) {
                mLoginViewModel.sendSingUpOtp(
                    mBinding.edMobile.text.toString().trim(),
                )
            } else {
                val dialog = InternetErrorDialogFragment()
                dialog.show(childFragmentManager, "internet")
            }
        }
        initView()
        observer()
    }


    private fun initView() {
        mBinding.apply {
            submit.setOnClickListener(this@SendOtpFragment)
            ivCross.setOnClickListener(this@SendOtpFragment)
        }
    }

    private fun observer() {
        activity?.let {
            mLoginViewModel.mSendSingUpOtpResponse.observe(it) { response ->

                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        response.data?.let { data ->
                            if (data.error != null) {
                                if (data.error) {
                                    it.showToast(data.message.toString())
                                } else {
                                    mBinding.apply {
                                        edOtp.visibility = View.VISIBLE
                                        edMobile.isFocusable = false
                                        edMobile.isClickable = false
                                        submit.text = resources.getString(R.string.verify_otp)
                                    }
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



            mLoginViewModel.mVerifySignUpOtpResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        response.data?.let { data ->
                            val gson =
                                GsonBuilder().setPrettyPrinting().create().toJson(response.data)
                            Log.e("datachecking", "" + gson)
                            if (data.error != null) {
                                if (data.error) {
                                    it.showToast(data.message.toString())
                                } else {
                                    data.response?.user.let { user ->
                                        if (user != null) {
                                            mPref.apply {
                                                mPref.setToken(data.response?.token)
                                                setID(user.id)
                                                setName(user.name)
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
                                            }
                                            startActivity(Intent(it, MainActivity::class.java))
                                            it.finish()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("verifyOtp_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.hideKeyboard()
                        it.showProgressDialog()
                        Log.e("verifyOtp_loading", "loading---->>>>")
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.submit -> {
                        if (submit.text == resources.getString(R.string.verify_otp)) {
                            if (isNetworkConnected) {
                                if (edMobile.text.toString().trim().isEmpty()) {
                                    it.showToast("Phone number can't be empty")
                                    return@let
                                } else if (edMobile.text.toString().trim().length != 10) {
                                    it.showToast("Phone number invalid")
                                    return@let
                                } else if (edOtp.text.isNullOrEmpty()) {
                                    it.showToast("Please enter otp")
                                    return@let
                                } else {
                                    mLoginViewModel.verifySignUpOtp(
                                        edOtp.text.toString().toInt(),
                                        edMobile.text.toString().trim()
                                    )
                                }
                            } else {
                                val dialog = InternetErrorDialogFragment()
                                dialog.show(childFragmentManager, "internet")
                               // it.showToast(resources.getString(R.string.check_your_internet))
                            }

                        } else {
                            if (edMobile.text.toString().trim().isEmpty()) {
                                it.showToast("Phone number can't be empty")
                                return@let
                            }
                            if (edMobile.text.toString().trim().length != 10) {
                                it.showToast("Phone number invalid")
                                return@let
                            } else {
                                if (isNetworkConnected) {
                                    mLoginViewModel.sendSingUpOtp(
                                        edMobile.text.toString().trim(),
                                    )
                                } else {
                                    val dialog = InternetErrorDialogFragment()
                                    dialog.show(childFragmentManager, "internet")
                                    //findNavController().navigate(R.id.internetErrorDialogFragment2)
                                }
                            }
                        }
                    }

                    R.id.iv_cross -> {
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}