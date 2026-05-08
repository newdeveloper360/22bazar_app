package com.userplay.bazar22.models

import com.google.gson.annotations.SerializedName

data class UpiMoneyResponse(
    @SerializedName("error")
    val error: Boolean=false,
    @SerializedName("message")
    val message: String="",
    @SerializedName("response")
    val response: ResponseUpiPay = ResponseUpiPay(),
    val isObserveable: Boolean=false,
)
data class ResponseUpiPay(
    @SerializedName("payment_link")
    val paymentLink: String="",
)