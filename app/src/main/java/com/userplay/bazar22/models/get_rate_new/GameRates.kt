package com.userplay.bazar22.models.get_rate_new


import com.google.gson.annotations.SerializedName

data class GameRates(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("game_type")
    val gameType: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("multiply_by")
    val multiplyBy: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)