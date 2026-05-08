package com.userplay.bazar22.models.verify_signup_otp


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("user")
    val user: User?,
    @SerializedName("token")
    val token: String="",
)