package com.userplay.bazar22.models.forgot_otp_verify


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("user")
    val user: User?
)