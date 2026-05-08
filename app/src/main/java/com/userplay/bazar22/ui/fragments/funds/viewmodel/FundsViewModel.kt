package com.userplay.bazar22.ui.fragments.funds.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.userplay.bazar22.models.getWithDrawHistory.GetWithDrawHistroyResponse
import com.userplay.bazar22.models.get_deposit_history.GetDepositHistoryResponse
import com.userplay.bazar22.models.save_bank_details.SaveBankDetails
import com.userplay.bazar22.models.saveupi.SaveUpiResponse
import com.userplay.bazar22.models.withdraw_balance.WithDrawBalanceResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FundsViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    var mSaveUpiResponse: MutableLiveData<ApiState<SaveUpiResponse>> = MutableLiveData()
    var mSaveBankDetailsResponse: MutableLiveData<ApiState<SaveBankDetails>> = MutableLiveData()
    var mGetDepositHistoryResponse: MutableLiveData<ApiState<GetDepositHistoryResponse>> =
        MutableLiveData()
    var mGetWithDrawHistoryResponse: MutableLiveData<ApiState<GetWithDrawHistroyResponse>> =
        MutableLiveData()
    var mWithDrawBalanceResponse: MutableLiveData<ApiState<WithDrawBalanceResponse>> =
        MutableLiveData()

    fun saveUpiDetails(name: String, upiID: String) {
        mSaveUpiResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.saveUpiDetails(name, upiID)
                mSaveUpiResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }


    fun saveBankDetails(name: String, number: String, ifsc: String) {
        mSaveBankDetailsResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.saveBankDetails(name, number, ifsc)
                mSaveBankDetailsResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }

    fun withdrawAmount(amount: String, mode: String) {
        mWithDrawBalanceResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getWithDrawBalance(amount, mode)
                mWithDrawBalanceResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }


    fun getDepositeList(pageNo: Int) {
        mGetDepositHistoryResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getDepositHistory(pageNo)
                mGetDepositHistoryResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }


    fun getWithdrawList(pageNo: Int) {
        mGetWithDrawHistoryResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getWithDrawHistory(pageNo)
                mGetWithDrawHistoryResponse.value = handleResponse(response)
            } catch (e: Exception) {
                Log.e("exception", "" + e.localizedMessage)
            }
        }
    }


    fun getDepositeResponse() = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100),
        pagingSourceFactory = { GetDepositeHistoryPagingSource(mApiInterface) }
    ).liveData.cachedIn(viewModelScope)


    fun getWithdrawResponse() = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100),
        pagingSourceFactory = { GetWithdrawHistoryPagingSource(mApiInterface) }
    ).liveData.cachedIn(viewModelScope)


}