package com.userplay.bazar22.models.pay_url


import com.google.gson.annotations.SerializedName

data class PayUrlResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)