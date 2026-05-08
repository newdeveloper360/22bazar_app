package com.userplay.bazar22.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.userplay.bazar22.R
import com.userplay.bazar22.databinding.FragmentSignUpBinding
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.ui.activities.MainActivity
import com.userplay.bazar22.ui.dialogs.ErrorDialogFragment
import com.userplay.bazar22.ui.dialogs.InternetErrorDialogFragment
import com.userplay.bazar22.ui.viewmodels.SignUpViewModel
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.dismissDialog
import com.userplay.bazar22.utils.showProgressDialog
import com.userplay.bazar22.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up), View.OnClickListener {

    @Inject
    lateinit var mPref: MatkaPref
    private lateinit var mBinding: FragmentSignUpBinding
    private val mSignUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(inflater : LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (this::mBinding.isInitialized) {
            mBinding
        } else {
            mBinding = FragmentSignUpBinding.inflate(inflater, container, false)
            initView()
            observer()
        }
        return mBinding.root
    }

    private fun initView() {
        mBinding.apply {
            signup.setOnClickListener(this@SignUpFragment)
            login.setOnClickListener(this@SignUpFragment)
            if(mPref.getEarningSystem()){
                tvInviteCode.visibility=View.VISIBLE
                edInviteCode.visibility=View.VISIBLE
            }else{
                tvInviteCode.visibility=View.GONE
                edInviteCode.visibility=View.GONE
            }
        }
    }

    private fun observer() {
        activity?.let {
            mSignUpViewModel.mSignUpResponse.observe(it) { response ->
                when (response) {
                    is ApiState.Success -> {
                        it.dismissDialog()

                        if(response.data?.error != null) {
                            if (response.data.error) {
                                val bundle = Bundle()
                                val dialog = ErrorDialogFragment()
                                bundle.putString("message", response.data.message.toString())
                                dialog.arguments = bundle
                                dialog.show(childFragmentManager, "error")
                            } else {
                                if(mBinding.edMobileNo.text.toString().trim()=="1234567890"){
                                    mPref.setMatkaEnable(true)
                                    /*val action =
                                        SignUpFragmentDirections.actionSignUpFragmentToMatkaFragment()
                                    findNavController().navigate(action)*/
                                        startActivity(
                                            Intent(
                                                it,
                                                MainActivity::class.java
                                            )
                                        )
                                        it.finish()
                                } else if (mPref.getOtpSysmteRemoved() == 1) {
                                    if(!response.data.token.isNullOrEmpty()){
                                        mPref.setToken(response.data.token?:"")
                                    }else {
                                        mPref.setToken(response.data.response?.token?:"")
                                    }
                                    mPref.setIsUserLogin(true)
                                    activity?.showToast("Signup successfully, Please Login to continue.")
                                    response.data.response?.user.let { user ->
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
                                } else {
                                    activity?.showToast("Signup successfully")
                                    val action =
                                        SignUpFragmentDirections.actionSignUpFragmentToSendOtpFragment(mBinding.edMobileNo.text.toString().trim())
                                    findNavController().navigate(action)
                                }

                            }
                        }
                    }

                    is ApiState.Error -> {
                        it.dismissDialog()
                        Log.e("login_error", "" + response.message)
                    }

                    is ApiState.Loading -> {
                        it.showProgressDialog()
                        Log.e("signup_loading", "loading---->>>>")
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        mBinding.apply {
            activity?.let {

                when (v?.id) {
                    R.id.login -> {
                        findNavController().popBackStack()
                    }

                    R.id.signup -> {

                        if (edName.text.toString().trim().isEmpty()) {
                            edName.error = "Name can't be empty"
                            return@let
                        }

                        if (edMobileNo.text.toString().trim().isEmpty()) {
                            edMobileNo.error = "Phone number can't be empty"
                            return@let
                        }

                        if (edMobileNo.text.toString().trim().length > 12 || edMobileNo.text.toString().trim().length < 10) {
                            edMobileNo.error = "Phone number invalid"
                            return@let
                        }

                        if(edMpin.text.toString().trim().isEmpty()) {
                            edMpin.error = "Mpin can't be empty"
                            return@let
                        }

                        if (edMpin.text.toString().trim().length != 4) {
                            edMpin.error = "Mpin should be 4 digits"
                            return@let
                        }

                        else {
                            if (CheckNetwork.isNetworkConnected) {
                                val referralCode = edInviteCode.text.toString().trim()
                                mSignUpViewModel.getSingUp(
                                    edMobileNo.text.toString().trim(),
                                    edMpin.text.toString().trim(),
                                    mPref.getFcmKey(Constants.FCM_KEY),
                                    edName.text.trim().toString(),
                                    if (referralCode.isNotEmpty()) referralCode else null,
                                    mPref.getAgentCode()
                                )
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
}