package com.userplay.bazar22.models.get_game_history


import com.google.gson.annotations.SerializedName

data class GetGameHistoryResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: Any?,
    @SerializedName("response")
    val response: Response?
)