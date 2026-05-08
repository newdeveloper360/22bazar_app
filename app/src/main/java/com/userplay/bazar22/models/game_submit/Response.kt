package com.userplay.bazar22.models.game_submit


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("balance_left")
    val balanceLeft: Double?
)