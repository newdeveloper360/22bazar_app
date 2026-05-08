package com.userplay.bazar22.models.user_level

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("userlevelsData")
    val userlevelsData: ArrayList<UserLevelData>,
    @SerializedName("total_earned")
    val totalEarned: Double?,
    @SerializedName("total_invited")
    val totalInvited: Int?
)
