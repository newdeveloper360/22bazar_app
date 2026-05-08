package com.userplay.bazar22.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentForgotDialogBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.ui.viewmodels.LoginViewModel
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.hideKeyboard
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentForgotDialogBinding
    private val mLoginViewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentForgotDialogBinding.inflate(inflater, container, false)
            initView()
            observer()
        }

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


    private fun initView() {
        mBinding.apply {
            submit.setOnClickListener(this@ForgotDialogFragment)
            ivCross.setOnClickListener(this@ForgotDialogFragment)
        }
    }

    private fun observer() {

        activity?.let {
            mLoginViewModel.mForgotOtpResponse.observe(it) { response ->

                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()
                        response.data?.let { data ->
                            if (data.error != null) {
                                if (data.error) {
                                    val bundle = Bundle()
                                    bundle.putString("message", response.data.message.toString())
                                    findNavController().navigate(
                                        R.id.errorDialogFragment2,
                                        bundle
                                    )
                                } else {
                                    dismiss()
                                    val action =
                                        ForgotDialogFragmentDirections.actionForgotDialogFragmentToSetNewPinFragment(
                                            mBinding.edMobile.text.toString()
                                        )

                                    findNavController().navigate(action)
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

    override fun onStart() {
        super.onStart()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {
                when (v?.id) {
                    R.id.submit -> {
                        if (edMobile.text.toString().trim().isEmpty()) {
                            it.showToast("Phone number can't be empty")
                            return@let
                        }
                        if (edMobile.text.toString().trim().length > 12 || edMobile.text.toString()
                                .trim().length < 10
                        ) {
                            it.showToast("Phone number invalid")
                            return@let
                        } else {
                            if (CheckNetwork.isNetworkConnected) {
                                it.hideKeyboard()
                                mLoginViewModel.forgotOtp(edMobile.text.toString())
                            } else {
                                val dialog = InternetErrorDialogFragment()
                                dialog.show(childFragmentManager, "internet")
                               // findNavController().navigate(R.id.internetErrorDialogFragment2)
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
}