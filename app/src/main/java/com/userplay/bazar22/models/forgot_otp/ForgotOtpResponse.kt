package com.userplay.bazar22.models.forgot_otp


import com.google.gson.annotations.SerializedName

data class ForgotOtpResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)