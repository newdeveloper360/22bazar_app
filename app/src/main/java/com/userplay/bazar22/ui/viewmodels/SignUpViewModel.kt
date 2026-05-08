package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.signup.SignUpResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    var mSignUpResponse: MutableLiveData<ApiState<SignUpResponse>> = MutableLiveData()


    fun getSingUp(
        phone: String?,
        password: String?,
        fcm: String?,
        userName: String?,
        referralCode: String?,
        agentCode: String?
    ) {
        mSignUpResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getSignup(
                    phone = phone,
                    password = password,
                    fcm = fcm,
                    userName = userName,
                    referralCode = referralCode,
                    agentCode = agentCode
                )
                mSignUpResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("get_app_data_exception", "" + e.localizedMessage)
            }
        }
    }

}