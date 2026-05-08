package com.userplay.bazar22.models.notification


import com.google.gson.annotations.SerializedName

data class NotificationChangeResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Any?
)