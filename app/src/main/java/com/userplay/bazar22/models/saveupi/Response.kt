package com.userplay.bazar22.models.saveupi


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("withdrawDetails")
    val withdrawDetails: WithdrawDetails?
)