package com.userplay.bazar22.models.get_game_history


import com.google.gson.annotations.SerializedName

data class GameType(
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