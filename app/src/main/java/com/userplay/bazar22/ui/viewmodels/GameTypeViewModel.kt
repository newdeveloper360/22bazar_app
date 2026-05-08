package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.models.SendBodyTotal
import com.userplay.bazar22.models.game_submit.GameSubmitResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameTypeViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {



    var mGameSubmitResponse: MutableLiveData<ApiState<GameSubmitResponse>> = MutableLiveData()


    fun gameSubmit(request: SendBody) {
        mGameSubmitResponse.value = ApiState.Loading()
        viewModelScope.launch {
           // try {
                val response = mApiInterface.submitGame(request)
                mGameSubmitResponse.value = handleResponse(response)

//            } catch (e: Exception) {
//                Log.e("game_submit", "" + e.localizedMessage)
            }
        //}
    }

    fun gameSubmitTotal(request: SendBodyTotal) {
        mGameSubmitResponse.value = ApiState.Loading()
        viewModelScope.launch {
            val response = mApiInterface.submitGameTotal(request)
            mGameSubmitResponse.value = handleResponse(response)
        }
    }

}