package com.userplay.bazar22.models.withdraw_balance


import com.google.gson.annotations.SerializedName

data class WithDrawBalanceResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)