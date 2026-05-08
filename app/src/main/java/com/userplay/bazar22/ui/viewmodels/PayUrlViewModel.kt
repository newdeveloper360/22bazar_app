package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse.AppDataResponse
import com.userplay.bazar22.models.pay_url.PayUrlResponse
import com.userplay.bazar22.models.payment_added.PaymentAddedResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.network.ApiState
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayUrlViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    var mPayUrlResponse: MutableLiveData<ApiState<PayUrlResponse>> = MutableLiveData()
    var mUpdateBalanceResponse: MutableLiveData<ApiState<PaymentAddedResponse>> = MutableLiveData()
    var listAmount: ArrayList<Int> = arrayListOf(
        pref.getMinDeposit(Constants.MIN_DEPOSIT),
        1000,
        5000,
        10000,
        20000,
        50000,
        100000
    )

    fun updateBalance(
        amount: Int?,
        pay_status: String?,
    ) {
        mUpdateBalanceResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.addPayment(
                    amount = amount,
                    pay_status=pay_status,
                )
                mUpdateBalanceResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("get_app_data_exception", "" + e.localizedMessage)
            }
        }
    }

    fun getPayUrl(
        amount: Int?,
    ) {
        mPayUrlResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getPayUrl(
                    amount = amount,
                )
                mPayUrlResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("get_app_data_exception", "" + e.localizedMessage)
            }
        }
    }

    var mPayFromUpiUrlResponse: MutableLiveData<ApiState<PayUrlResponse>> = MutableLiveData()
    fun getPayFromUpiUrl(
        amount: Int?,
    ) {
        mPayFromUpiUrlResponse.value = ApiState.Loading()
        viewModelScope.launch {
            try {
                val response = mApiInterface.getPayFromUpiUrl(
                    amount = amount,
                )
                mPayFromUpiUrlResponse.value = handleResponse(response)

            } catch (e: Exception) {
                Log.e("get_app_data_exception", "" + e.localizedMessage)
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