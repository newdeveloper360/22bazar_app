package com.userplay.bazar22.models.getpassbook


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("current_amount")
    val currentAmount: Double?,
    @SerializedName("details")
    val details: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("previous_amount")
    val previousAmount: Double?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_id")
    val userId: Int?
)