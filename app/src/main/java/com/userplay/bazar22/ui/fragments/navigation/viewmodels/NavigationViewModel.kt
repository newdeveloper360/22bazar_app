package com.userplay.bazar22.ui.fragments.navigation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.get_rate_new.GameRateResponseNew
import com.userplay.bazar22.models.notification.NotificationChangeResponse
import com.userplay.bazar22.models.refferal.RefferalResponse
import com.userplay.bazar22.models.user_level.UserLevelResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    var mNotificationChangeResponse: MutableLiveData<ApiState<NotificationChangeResponse>> =
        MutableLiveData()

    var mReferralResponse: MutableLiveData<ApiState<RefferalResponse>> =
        MutableLiveData()

    var mGameRatesResponse: MutableLiveData<ApiState<GameRateResponseNew>> = MutableLiveData()

    var mUserLevelResponse: MutableLiveData<ApiState<UserLevelResponse>> = MutableLiveData()



    fun changeNotification(
        type: String?
    ) {
        mNotificationChangeResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.changeNotification(type = type)
                mNotificationChangeResponse.value = Constants.handleResponse(response)

            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }

    fun getReferral() {
        mReferralResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getReferralDetails()
                mReferralResponse.value = Constants.handleResponse(response)

            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }

    fun getUserLevel(levelId: Int?) {
        mUserLevelResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getUserLevels(levelId)
                mUserLevelResponse.value = Constants.handleResponse(response)

            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
                mUserLevelResponse.value = ApiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getGameRates() {
        mGameRatesResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getGameRates()
                mGameRatesResponse.value = Constants.handleResponse(response)

            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

}