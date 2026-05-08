package com.userplay.bazar22.models

import com.google.gson.annotations.SerializedName

data class PayUrlIntentResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("response")
    var response: ResponseIntent = ResponseIntent()
)

data class ResponseIntent(
    @SerializedName("upiIntent")
    var upiIntent: String = ""
)

