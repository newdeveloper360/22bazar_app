package com.userplay.bazar22.models.login


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("token")
    val token: String?,
    @SerializedName("user")
    val user: User?
)