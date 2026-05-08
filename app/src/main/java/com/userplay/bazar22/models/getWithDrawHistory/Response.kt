package com.userplay.bazar22.models.getWithDrawHistory


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("depositHistory")
    val depositHistory: DepositHistory?
)