package com.userplay.bazar22.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.userplay.bazar22.models.RedeemGiftResponse
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.preferences.MatkaPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedeemGiftViewModel @Inject constructor(
    application: Application,
    private val mApiInterface: ApiInterface,
    var pref: MatkaPref
) : AndroidViewModel(application) {

    fun redeemGift(
        code: String,
        onSuccess: (RedeemGiftResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
                val response = mApiInterface.redeemGift(code)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.error) {

                            onSuccess(it)
                        } else {
                            onFailure(it.message)
                        }
                    } ?: run {
                        onFailure("We are unable to process your request try again.")
                    }
                } else {
                    onFailure("We are unable to process your request try again.")
                }

            }
        }



}