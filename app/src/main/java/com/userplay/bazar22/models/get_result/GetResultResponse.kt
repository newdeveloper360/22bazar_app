package com.userplay.bazar22.models.get_result


import com.google.gson.annotations.SerializedName

data class GetResultResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val response: Response?
)