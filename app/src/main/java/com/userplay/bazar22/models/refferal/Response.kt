package com.userplay.bazar22.models.refferal


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("referralUsers")
    val referralUsers: ArrayList<ReferralUser>,
    @SerializedName("total_earned")
    val totalEarned: Int?,
    @SerializedName("total_invited")
    val totalInvited: Int?
)