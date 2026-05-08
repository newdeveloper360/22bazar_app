package com.userplay.bazar22.models.withdraw_balance


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("balance_left")
    val balanceLeft: Double?
)