package com.userplay.bazar22.models.signup


import com.google.gson.annotations.SerializedName
import com.userplay.bazar22.models.login.User

data class Response(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("user")
    val user: User?
)