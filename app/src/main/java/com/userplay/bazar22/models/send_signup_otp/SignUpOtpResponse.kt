package com.userplay.bazar22.models.send_signup_otp


import com.google.gson.annotations.SerializedName

data class SignUpOtpResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("otp")
    val otp: Int?
)