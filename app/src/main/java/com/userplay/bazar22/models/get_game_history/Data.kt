package com.userplay.bazar22.models.get_game_history


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("game_string")
    val gameString: String?,
    @SerializedName("game_type")
    val gameType: GameType?,
    @SerializedName("game_type_id")
    val gameTypeId: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("market")
    val market: Market?,
    @SerializedName("market_id")
    val marketId: Int?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("session")
    val session: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("win_amount")
    val winAmount: Any?
)