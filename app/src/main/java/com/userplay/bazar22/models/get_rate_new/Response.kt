package com.userplay.bazar22.models.get_rate_new


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("data")
    val `data`: ArrayList<Data>?
)