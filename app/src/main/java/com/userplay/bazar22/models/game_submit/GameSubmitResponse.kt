package com.userplay.bazar22.models.game_submit


import com.google.gson.annotations.SerializedName

data class GameSubmitResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)