package com.userplay.bazar22.models.refferal


import com.google.gson.annotations.SerializedName

data class RefferalResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)