package com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse


import com.google.gson.annotations.SerializedName

data class AppDataResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)