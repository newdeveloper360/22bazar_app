package com.userplay.bazar22.models.user_level

import com.google.gson.annotations.SerializedName

data class UserLevelResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)
