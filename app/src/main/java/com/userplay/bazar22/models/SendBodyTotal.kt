package com.userplay.bazar22.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendBodyTotal(
    @SerializedName("game_type_id")
    val gameTypeId: Int?,
    @SerializedName("games")
    val games: List<GameTotal>,
    @SerializedName("market_id")
    val marketId: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("is_total_jodi")
    val isTotalJodi: Boolean=true
) : Serializable