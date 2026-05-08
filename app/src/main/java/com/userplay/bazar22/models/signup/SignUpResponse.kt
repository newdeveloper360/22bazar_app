package com.userplay.bazar22.models.signup


import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?,
    @SerializedName("token")
    val token: String?,
)