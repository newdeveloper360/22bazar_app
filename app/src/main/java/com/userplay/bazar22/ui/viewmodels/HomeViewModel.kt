package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse.AppDataResponse
import com.userplay.bazar22.models.get_markets.GeneralMarketResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.GENERAL_MARKET
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    var mMarketResponse: MutableLiveData<ApiState<GeneralMarketResponse>> = MutableLiveData()


    fun getGeneralMarket() {
        mMarketResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getMarket(GENERAL_MARKET)
                mMarketResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

    fun getDesawarMarket() {
        mMarketResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getMarket(Constants.DESAWAR_MARKET)
                mMarketResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }


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