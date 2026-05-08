package com.userplay.bazar22.models.get_game_history


import com.google.gson.annotations.SerializedName

data class Market(
    @SerializedName("api_key_name")
    val apiKeyName: String?,
    @SerializedName("auto_result")
    val autoResult: Int?,
    @SerializedName("close_game_status")
    val closeGameStatus: Boolean?,
    @SerializedName("close_result_time")
    val closeResultTime: String?,
    @SerializedName("close_time")
    val closeTime: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("disable_game")
    val disableGame: Int?,
    @SerializedName("game_on")
    val gameOn: Boolean?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("last_result")
    val lastResult: LastResult?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("open_game_status")
    val openGameStatus: Boolean?,
    @SerializedName("open_result_time")
    val openResultTime: String?,
    @SerializedName("open_time")
    val openTime: String?,
    @SerializedName("previous_day_check")
    val previousDayCheck: Int?,
    @SerializedName("saturday_open")
    val saturdayOpen: Int?,
    @SerializedName("sunday_open")
    val sundayOpen: Int?,
    @SerializedName("updated_at")
    val updatedAt: String?
)