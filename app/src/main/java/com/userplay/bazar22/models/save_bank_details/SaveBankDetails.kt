package com.userplay.bazar22.models.save_bank_details

import com.google.gson.annotations.SerializedName

data class SaveBankDetails(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)