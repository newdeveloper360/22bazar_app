package com.userplay.bazar22.models.saveupi


import com.google.gson.annotations.SerializedName

data class SaveUpiResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)