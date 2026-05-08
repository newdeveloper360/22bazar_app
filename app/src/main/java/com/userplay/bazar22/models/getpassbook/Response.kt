package com.userplay.bazar22.models.getpassbook


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("transactions")
    val transactions: Transactions?
)