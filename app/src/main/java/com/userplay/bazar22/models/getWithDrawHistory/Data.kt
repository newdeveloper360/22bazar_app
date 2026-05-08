package com.userplay.bazar22.models.getWithDrawHistory


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
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
    @SerializedName("withdraw_mode")
    val withdrawMode: String?,
    @SerializedName("transaction_id")
    val transactionId: String?
)