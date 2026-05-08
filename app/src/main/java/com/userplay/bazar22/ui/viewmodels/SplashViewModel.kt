package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse.AppDataResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

//    var mGetAppDataResponse: MutableLiveData<ApiState<GetAppDataResponse>> = MutableLiveData()
    var mGetAppDataResponse: MutableLiveData<ApiState<AppDataResponse>> = MutableLiveData()


    fun getAppData() {
        mGetAppDataResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getAppData()
                mGetAppDataResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("get_app_data_exception", "" + e.localizedMessage)
            }
        }
    }
}