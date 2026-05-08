package com.userplay.bazar22.models.get_game_history


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("gameHistory")
    val gameHistory: GameHistory?
)