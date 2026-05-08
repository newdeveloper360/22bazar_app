package com.userplay.bazar22.models.verify_signup_otp

import com.google.gson.annotations.SerializedName

data class VerifySignUpOtpResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)