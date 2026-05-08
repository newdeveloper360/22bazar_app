package com.userplay.bazar22.models.get_rate_new


import com.google.gson.annotations.SerializedName

data class GameRateResponseNew(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)