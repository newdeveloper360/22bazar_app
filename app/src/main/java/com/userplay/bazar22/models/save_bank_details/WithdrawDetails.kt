package com.userplay.bazar22.models.save_bank_details


import com.google.gson.annotations.SerializedName

data class WithdrawDetails(
    @SerializedName("account_holder_name")
    val accountHolderName: String?,
    @SerializedName("account_ifsc_code")
    val accountIfscCode: String?,
    @SerializedName("account_number")
    val accountNumber: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("upi_id")
    val upiId: String?,
    @SerializedName("upi_name")
    val upiName: String?,
    @SerializedName("user_id")
    val userId: Int?
)