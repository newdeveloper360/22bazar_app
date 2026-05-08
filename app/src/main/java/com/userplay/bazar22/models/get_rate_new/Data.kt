package com.userplay.bazar22.models.get_rate_new


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("list")
    val list: ArrayList<GameRates>,
    @SerializedName("title")
    val title: String?
)