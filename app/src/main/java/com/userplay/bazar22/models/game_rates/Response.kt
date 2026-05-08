package com.userplay.bazar22.models.game_rates


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("gameTypes")
    val gameTypes: ArrayList<GameType>
)