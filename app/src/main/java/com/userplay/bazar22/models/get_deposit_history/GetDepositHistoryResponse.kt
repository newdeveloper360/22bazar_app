package com.userplay.bazar22.models.get_deposit_history


import com.google.gson.annotations.SerializedName

data class GetDepositHistoryResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)