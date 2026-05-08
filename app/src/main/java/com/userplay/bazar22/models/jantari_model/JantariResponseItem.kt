package com.userplay.bazar22.models.jantari_model


import com.google.gson.annotations.SerializedName

data class JantariResponseItem(
    @SerializedName("numbers")
    val numbers: ArrayList<Number>,
    @SerializedName("title")
    val title: String?
)