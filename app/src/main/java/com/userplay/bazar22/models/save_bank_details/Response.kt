package com.userplay.bazar22.models.save_bank_details


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("withdrawDetails")
    val withdrawDetails: WithdrawDetails?
)