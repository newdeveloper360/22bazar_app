package com.userplay.bazar22.models.user_level

import com.google.gson.annotations.SerializedName

data class UserLevelData (
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("registered_at")
    val createdAt: String?,
    @SerializedName("today_commission")
    val todayCommission: Double?,
    @SerializedName("total_commission")
    val totalCommission: Double?
)