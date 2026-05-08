package com.userplay.bazar22.models.forgot_otp


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("otp")
    val otp: Int?
)