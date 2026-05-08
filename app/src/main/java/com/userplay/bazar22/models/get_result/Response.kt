package com.userplay.bazar22.models.get_result


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("gameResults")
    val gameResults: ArrayList<GameResult>?
)