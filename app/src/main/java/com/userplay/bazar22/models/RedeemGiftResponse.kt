package com.userplay.bazar22.models

import com.google.gson.annotations.SerializedName

data class RedeemGiftResponse(
    @SerializedName("error")
    val error: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("response")
    val response: RedeemBalance = RedeemBalance()
)

data class RedeemBalance(
    @SerializedName("userBalance")
    val userBalance: Int = 0,
    val amountWon: Int = 0,
)