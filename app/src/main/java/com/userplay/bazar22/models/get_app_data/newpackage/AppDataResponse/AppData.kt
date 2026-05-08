package com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse


import com.google.gson.annotations.SerializedName

data class AppData(
    @SerializedName("user_level_system")
    val userLevelSystem: Boolean=false,
    @SerializedName("otp_system_removed")
    val otpSystemRemoved: Int=0,
    @SerializedName("admin_upi")
    val adminUpi: String?,
    @SerializedName("app_update_link")
    val appUpdateLink: String?,
    @SerializedName("auto_result_api")
    val autoResultApi: Any?,
    @SerializedName("bank_withdraw_enable")
    val bankWithdrawEnable: Int?,
    @SerializedName("charts_url")
    val chartsUrl: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("enable_desawar")
    val enableDesawar: Int?,
    @SerializedName("enable_desawar_only")
    val enableDesawarOnly: Int?,
    @SerializedName("maintain_mode")
    val maintainMode: Int?,
    @SerializedName("fcm_key")
    val fcmKey: String?,
    @SerializedName("home_message")
    val homeMessage: String?,
    @SerializedName("homepage_image_url")
    val homepageImageUrl: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("invite_bonus")
    val inviteBonus: Int?,
    @SerializedName("invite_system_enable")
    val inviteSystemEnable: Int?,
    @SerializedName("min_deposit")
    val minDeposit: Int?,
    @SerializedName("min_withdraw")
    val minWithdraw: Int?,
    @SerializedName("min_bid_amount")
    val minBidAmount: Int?,
    @SerializedName("payment_method")
    val paymentMethod: String?,
    @SerializedName("payment_url")
    val paymentUrl: String?,
    @SerializedName("rules_notice")
    val rulesNotice: String?,
    @SerializedName("slider_url")
    val sliderUrl: String?,
    @SerializedName("sms_api_key")
    val smsApiKey: String?,
    @SerializedName("support_number")
    val supportNumber: String?,
    @SerializedName("support_time")
    val supportTime: String?,
    @SerializedName("telegram_enable")
    val telegramEnable: Int?,
    @SerializedName("telegram_link")
    val telegramLink: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("upi_withdraw_enable")
    val upiWithdrawEnable: Int?,
    @SerializedName("version")
    val version: Int?,
    @SerializedName("welcome_bonus")
    val welcomeBonus: Int?,
    @SerializedName("whatsapp_enable")
    val whatsappEnable: Int?,
    @SerializedName("whatsapp_number")
    val whatsappNumber: String?,
    @SerializedName("withdraw_close_time")
    val withdrawCloseTime: String?,
    @SerializedName("withdraw_open_time")
    val withdrawOpenTime: String?,
    @SerializedName("withdrawal_condition")
    val withdrawalCondition: String?,
    @SerializedName("play_store")
    val playStoreEnable: Int?,
    @SerializedName("show_results_only")
    val showResultsOnly: Int=0,
)