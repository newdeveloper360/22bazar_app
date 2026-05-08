package com.userplay.bazar22.models.get_markets


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("markets")
    val markets: List<Market>?
)