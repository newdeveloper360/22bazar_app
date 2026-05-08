package com.userplay.bazar22.models.get_deposit_history


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("depositHistory")
    val depositHistory: DepositHistory?
)