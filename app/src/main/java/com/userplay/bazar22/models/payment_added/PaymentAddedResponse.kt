package com.userplay.bazar22.models.payment_added


import com.google.gson.annotations.SerializedName

data class PaymentAddedResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)