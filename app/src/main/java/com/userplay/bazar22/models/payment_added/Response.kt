package com.userplay.bazar22.models.payment_added


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("balance_left")
    val balanceLeft: Double?
)