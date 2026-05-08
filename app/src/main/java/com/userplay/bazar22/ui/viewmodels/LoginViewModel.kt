package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.forgot_otp.ForgotOtpResponse
import com.userplay.bazar22.models.forgot_otp_verify.ForgotOtpVerifyResponse
import com.userplay.bazar22.models.login.LoginResponse
import com.userplay.bazar22.models.send_signup_otp.SignUpOtpResponse
import com.userplay.bazar22.models.verify_signup_otp.VerifySignUpOtpResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    var mLoginResponse: MutableLiveData<ApiState<LoginResponse>> = MutableLiveData()
    var mSendSingUpOtpResponse: MutableLiveData<ApiState<SignUpOtpResponse>> = MutableLiveData()
    var mVerifySignUpOtpResponse: MutableLiveData<ApiState<VerifySignUpOtpResponse>> =
        MutableLiveData()
    var mForgotOtpResponse: MutableLiveData<ApiState<ForgotOtpResponse>> = MutableLiveData()
    var mVerifyForGotOtpResponse: MutableLiveData<ApiState<ForgotOtpVerifyResponse>> =
        MutableLiveData()


    fun getLogin(
        phone: String?,
        mPin: Int?,
        fcm: String?) {
        mLoginResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getLogin(
                    phone = phone,
                    mPin = mPin,
                    fcm = fcm
                )
                mLoginResponse.value = handleResponse(response)

            } catch (e: Exception) {
                ApiState.Error(e.localizedMessage,"")
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

    fun sendSingUpOtp(phone: String?) {
        mSendSingUpOtpResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.sendSignUpOtp(phone = phone)
                mSendSingUpOtpResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }


    fun verifySignUpOtp(otp: Int?, phone: String?) {
        mVerifySignUpOtpResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.verifySignUpOtp(otp = otp, phone = phone)
                mVerifySignUpOtpResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

    fun forgotOtp(phone: String?) {
        mForgotOtpResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.forgotOtp(phone = phone)
                mForgotOtpResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

    fun verifyForgotOtp(phone: String, otp: String?, mpin: String) {
        Log.e("data check", "$phone $otp $mpin")
        mVerifyForGotOtpResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.verifyForgotOtp(
                    phone = phone,
                    mpin = mpin,
                    otp = otp
                )
                mVerifyForGotOtpResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }
}