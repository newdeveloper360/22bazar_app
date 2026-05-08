package com.userplay.bazar22.models.getpassbook


import com.google.gson.annotations.SerializedName

data class GetPassbookResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)