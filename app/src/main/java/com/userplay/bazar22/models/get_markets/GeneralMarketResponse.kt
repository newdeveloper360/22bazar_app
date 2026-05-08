package com.userplay.bazar22.models.get_markets


import com.google.gson.annotations.SerializedName

data class GeneralMarketResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)