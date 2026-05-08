package com.userplay.bazar22.models.jantari_model


import com.google.gson.annotations.SerializedName

data class Number(
    @SerializedName("number")
    val number: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("gameTypeId")
    val gameTypeId: Int
)