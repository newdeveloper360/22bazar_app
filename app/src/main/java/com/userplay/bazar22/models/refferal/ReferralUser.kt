package com.userplay.bazar22.models.refferal


import com.google.gson.annotations.SerializedName

data class ReferralUser(
    @SerializedName("balance")
    val balance: Int?,
    @SerializedName("blocked")
    val blocked: Int?,
    @SerializedName("bonus")
    val bonus: Int?,
    @SerializedName("confirmed")
    val confirmed: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("desawar_noti")
    val desawarNoti: Int?,
    @SerializedName("fcm")
    val fcm: Any?,
    @SerializedName("general_noti")
    val generalNoti: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("last_logged_id")
    val lastLoggedId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("own_code")
    val ownCode: Int?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("startline_noti")
    val startlineNoti: Int?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_id")
    val userId: Int?
)