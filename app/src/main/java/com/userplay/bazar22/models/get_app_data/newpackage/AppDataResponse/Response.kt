package com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("appData")
    val appData: AppData?,
    @SerializedName("user")
    val user: User?
)