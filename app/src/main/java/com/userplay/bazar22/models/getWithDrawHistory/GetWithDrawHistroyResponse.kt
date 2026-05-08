package com.userplay.bazar22.models.getWithDrawHistory


import com.google.gson.annotations.SerializedName

data class GetWithDrawHistroyResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)