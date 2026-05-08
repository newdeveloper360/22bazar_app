package com.userplay.bazar22.models.game_rates


import com.google.gson.annotations.SerializedName

data class GameRatesResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)