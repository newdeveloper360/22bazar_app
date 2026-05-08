package com.userplay.bazar22.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendBody(
    @SerializedName("game_type_id")
    val gameTypeId: Int?,
    @SerializedName("games")
    val games: List<Game>,
    @SerializedName("market_id")
    val marketId: Int?,
    @SerializedName("type")
    val type: String?
) : Serializable