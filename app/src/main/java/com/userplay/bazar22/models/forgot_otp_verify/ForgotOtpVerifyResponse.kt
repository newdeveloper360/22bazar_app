package com.userplay.bazar22.models.forgot_otp_verify


import com.google.gson.annotations.SerializedName

data class ForgotOtpVerifyResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)