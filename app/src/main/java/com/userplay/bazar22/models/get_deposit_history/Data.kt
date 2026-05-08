package com.userplay.bazar22.models.get_deposit_history


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("deposit_mode")
    val depositMode: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("request_type")
    val requestType: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("transaction_id")
    val transactionId: String?
)