package com.userplay.bazar22.ui.fragments.mybids.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.get_game_history.GetGameHistoryResponse
import com.userplay.bazar22.models.get_result.GetResultResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBidsViewModels @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {


    var mGetResultResponse: MutableLiveData<ApiState<GetResultResponse>> = MutableLiveData()
    var mGetGameHistoryResponse: MutableLiveData<ApiState<GetGameHistoryResponse>> =
        MutableLiveData()


    fun getGameResult(type: String, date: String) {
        mGetResultResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getGamesResult(type, date)
                mGetResultResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }

    fun getGameHistory(type: String, pageNo: Int) {
        mGetGameHistoryResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getGameHistory(type, pageNo)
                mGetGameHistoryResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }
}