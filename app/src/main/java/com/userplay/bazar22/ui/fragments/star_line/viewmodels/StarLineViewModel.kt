package com.userplay.bazar22.ui.fragments.star_line.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.get_markets.GeneralMarketResponse
import com.userplay.bazar22.models.get_rate_new.GameRateResponseNew
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.STARLINE_MARKET
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StarLineViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {


    var mGameRatesResponse: MutableLiveData<ApiState<GameRateResponseNew>> = MutableLiveData()

    var mMarketResponse: MutableLiveData<ApiState<GeneralMarketResponse>> = MutableLiveData()



    fun getStarLineMarket() {
        mMarketResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getMarket(STARLINE_MARKET)
                mMarketResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }




    fun getGameRates() {
        mGameRatesResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getGameRates()
                mGameRatesResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

}