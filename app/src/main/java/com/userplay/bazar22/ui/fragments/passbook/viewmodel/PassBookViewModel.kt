package com.userplay.bazar22.ui.fragments.passbook.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.getpassbook.GetPassbookResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PassBookViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {


    var mGetPassbookResponse: MutableLiveData<ApiState<GetPassbookResponse>> = MutableLiveData()
//
//
    fun getPassBook(pageNumber : Int) {
        mGetPassbookResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getPassBook(pageNumber)
                mGetPassbookResponse.value = Constants.handleResponse(response)
            } catch (e: Exception) {
                Log.e("login_exception", "" + e.localizedMessage)
            }
        }
    }

//    fun getPassBook() = Pager(
//        config = PagingConfig(pageSize = 10, maxSize = 100),
//        pagingSourceFactory = { PassBookPagingSource(mApiInterface) }
//    ).liveData.cachedIn(viewModelScope)


}