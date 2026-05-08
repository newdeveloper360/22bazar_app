package com.userplay.bazar22.models.get_result


import com.google.gson.annotations.SerializedName

data class LastResult(
    @SerializedName("close_digit")
    val closeDigit: Int?,
    @SerializedName("close_pana")
    val closePana: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("market_id")
    val marketId: Int?,
    @SerializedName("open_digit")
    val openDigit: Int?,
    @SerializedName("open_pana")
    val openPana: Int?,
    @SerializedName("result")
    val result: String?,
    @SerializedName("result_date")
    val resultDate: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)