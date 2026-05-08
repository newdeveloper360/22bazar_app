package com.userplay.bazar22.models.pay_url


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("openInAndroidWebViewOnly")
    val openWebView: Boolean?,
    @SerializedName("payment_url")
    val paymentUrl: String?,
)