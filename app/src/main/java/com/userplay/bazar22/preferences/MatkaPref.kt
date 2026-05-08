package com.userplay.bazar22.preferences

import android.content.Context
import com.userplay.bazar22.utils.Constants
import com.userplay.bazar22.utils.Constants.ACCOUNT_HOLDER_NAME
import com.userplay.bazar22.utils.Constants.ACCOUNT_NUMBER
import com.userplay.bazar22.utils.Constants.ADMIN_UPI
import com.userplay.bazar22.utils.Constants.APP_UPDATE_LINK
import com.userplay.bazar22.utils.Constants.AUTO_RESULT_API
import com.userplay.bazar22.utils.Constants.BALANCE
import com.userplay.bazar22.utils.Constants.BANK_WITHDRAW_ENABLE
import com.userplay.bazar22.utils.Constants.BLOCKED
import com.userplay.bazar22.utils.Constants.BONUS
import com.userplay.bazar22.utils.Constants.CHART_URL
import com.userplay.bazar22.utils.Constants.CONFIRMED
import com.userplay.bazar22.utils.Constants.DESAWAR_NOTIFICATION
import com.userplay.bazar22.utils.Constants.ENABLE_DESAWAR_ONLY
import com.userplay.bazar22.utils.Constants.FCM_KEY
import com.userplay.bazar22.utils.Constants.GENERAL_NOTIFICATION
import com.userplay.bazar22.utils.Constants.HOME_MESSAGE
import com.userplay.bazar22.utils.Constants.ID
import com.userplay.bazar22.utils.Constants.IFSC_CODE
import com.userplay.bazar22.utils.Constants.IMAGE_URL
import com.userplay.bazar22.utils.Constants.INVITE_BONUS
import com.userplay.bazar22.utils.Constants.INVITE_SYSTEM_ENABLE
import com.userplay.bazar22.utils.Constants.IS_USER_LOGIN
import com.userplay.bazar22.utils.Constants.MAINTAIN_MODE
import com.userplay.bazar22.utils.Constants.MIN_BID
import com.userplay.bazar22.utils.Constants.MIN_DEPOSIT
import com.userplay.bazar22.utils.Constants.MIN_WITHDRAW
import com.userplay.bazar22.utils.Constants.NAME
import com.userplay.bazar22.utils.Constants.OTP_SYSTEM_REMOVED
import com.userplay.bazar22.utils.Constants.OWN_CODE
import com.userplay.bazar22.utils.Constants.PAYMENT_METHOD
import com.userplay.bazar22.utils.Constants.PHONE
import com.userplay.bazar22.utils.Constants.PILLERS
import com.userplay.bazar22.utils.Constants.PLAY_STORE_ENABLE
import com.userplay.bazar22.utils.Constants.ROLE
import com.userplay.bazar22.utils.Constants.RULE_NOTICE
import com.userplay.bazar22.utils.Constants.SESSION_TYPE
import com.userplay.bazar22.utils.Constants.SHARED_PRE_FILE
import com.userplay.bazar22.utils.Constants.SHOW_RESULTS_ONLY
import com.userplay.bazar22.utils.Constants.SLIDER
import com.userplay.bazar22.utils.Constants.SMS_API_KEY
import com.userplay.bazar22.utils.Constants.START_LINE_NOTIFICATION
import com.userplay.bazar22.utils.Constants.SUPPORT_NUMBER
import com.userplay.bazar22.utils.Constants.SUPPORT_TIME
import com.userplay.bazar22.utils.Constants.TELEGRAM_ENABLE
import com.userplay.bazar22.utils.Constants.TELEGRAM_LINK
import com.userplay.bazar22.utils.Constants.TOKEN
import com.userplay.bazar22.utils.Constants.UPI_WITHDRAW_ENABLE
import com.userplay.bazar22.utils.Constants.USER_ID
import com.userplay.bazar22.utils.Constants.USER_UPI
import com.userplay.bazar22.utils.Constants.VERSION
import com.userplay.bazar22.utils.Constants.WELCOME_BONUS
import com.userplay.bazar22.utils.Constants.WHATSAPP_ENABLE
import com.userplay.bazar22.utils.Constants.WHATSAPP_NUMBER
import com.userplay.bazar22.utils.Constants.WITHDRAW_CLOSE_TIME
import com.userplay.bazar22.utils.Constants.WITHDRAW_CONDITION
import com.userplay.bazar22.utils.Constants.WITHDRAW_OPEN_TIME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatkaPref @Inject constructor(@ApplicationContext context: Context) {


    private var preference = context.getSharedPreferences(SHARED_PRE_FILE, Context.MODE_PRIVATE)
    private var editor = preference.edit()

    fun setAgentCode(agent_code: String?) {
        editor.putString("agent_code", agent_code).apply()
    }

    fun getAgentCode(): String? {
        return preference.getString("agent_code", null)
    }

    fun setIsSubscribedToTopic(isUserLogin: Boolean?) {
        isUserLogin?.let { editor.putBoolean("subscribed_to_topic", it).apply() }
    }

    fun getIsSubscribedToTopic(): Boolean {
        return preference.getBoolean("subscribed_to_topic", false)
    }

    fun  setUserLevelSystem(isUserLevelSystem: Boolean?){
        isUserLevelSystem?.let { editor.putBoolean("user_level_system", it).apply() }
    }

    fun  getUserLevelSystem(): Boolean {
        return preference.getBoolean("user_level_system", false)
    }

    fun setOtpSysmteRemoved(enable: Int?) {
        enable?.let { editor.putInt(OTP_SYSTEM_REMOVED, it).apply() }
    }

    fun getOtpSysmteRemoved(): Int {
        return preference.getInt(OTP_SYSTEM_REMOVED, 0)
    }

    fun setToken(token: String?) {
        editor.putString(TOKEN, token).apply()
    }

    fun getToken(token: String): String? {
        return preference.getString(token, null)
    }

    fun setSessionType(type: String?) {
        editor.putString(SESSION_TYPE, type).apply()
    }

    fun getSessionType(type: String): String? {
        return preference.getString(type, "open")
    }


    fun setHomeMessage(message: String?) {
        editor.putString(HOME_MESSAGE, message).apply()
    }

    fun getHomeMessage(key: String): String? {
        return preference.getString(key, "")
    }

    fun setSupportNumber(supportNumber: String?) {
        editor.putString(SUPPORT_NUMBER, supportNumber).apply()
    }

    fun getSupportNumber(key: String): String? {
        return preference.getString(key, "")
    }

    fun setSupportTime(supportTime: String?) {
        editor.putString(SUPPORT_TIME, supportTime).apply()
    }

    fun getSupportTime(key: String): String? {
        return preference.getString(key, null)
    }

    fun setWithDrawCondition(condition: String?) {
        editor.putString(WITHDRAW_CONDITION, condition).apply()
    }

    fun getWithDrawCondition(key: String): String? {
        return preference.getString(key, null)
    }


    fun setRuleNotice(ruleNotice: String?) {
        editor.putString(RULE_NOTICE, ruleNotice).apply()
    }

    fun getRuleNotice(key: String): String? {
        return preference.getString(key, null)
    }

    fun setChartsUrl(ruleNotice: String?) {
        editor.putString(CHART_URL, ruleNotice).apply()
    }

    fun getChartsUrl(key: String): String? {
        return preference.getString(key, null)
    }

    fun setMinWithdraw(minWithdraw: Int?) {
        minWithdraw?.let { editor.putInt(MIN_WITHDRAW, it).apply() }
    }

    fun getMinWithdraw(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setMinBid(minBid: Int?) {
        minBid?.let { editor.putInt(MIN_BID, it).apply() }
    }

    fun getMinBid(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setMinDeposit(minDeposit: Int?) {
        minDeposit?.let { editor.putInt(MIN_DEPOSIT, it).apply() }
    }

    fun getMinDeposit(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setInviteBonus(bonus: Int?) {
        bonus?.let { editor.putInt(INVITE_BONUS, it).apply() }
    }

    fun getInviteBonus(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setInviteSystemEnable(enable: Int?) {
        enable?.let { editor.putInt(INVITE_SYSTEM_ENABLE, it).apply() }
    }

    fun getInviteSystemEnable(): Int {
        return preference.getInt(INVITE_SYSTEM_ENABLE, 0)
    }

    fun setWelcomeBonus(bonus: Int?) {
        bonus?.let { editor.putInt(WELCOME_BONUS, it).apply() }
    }

    fun getWelcomeBonus(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setAdminUpi(upi: String?) {
        editor.putString(ADMIN_UPI, upi).apply()
    }

    fun getAdminUpi(key: String): String? {
        return preference.getString(key, "")
    }


    fun setTelegramEnable(enable: Int?) {
        enable?.let { editor.putInt(TELEGRAM_ENABLE, it).apply() }
    }

    fun getTelegramEnable(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setTelegramLink(link: String?) {
        editor.putString(TELEGRAM_LINK, link).apply()
    }

    fun getTelegramLink(key: String): String? {
        return preference.getString(key, "")
    }


    fun setWhatsAppEnable(enable: Int?) {
        enable?.let { editor.putInt(WHATSAPP_ENABLE, it).apply() }
    }

    fun getWhatsAppEnable(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun setWhatsAppNumber(number: String?) {
        number.let { editor.putString(WHATSAPP_NUMBER, it).apply() }
    }

    fun getWhatsAppNumber(key: String): String? {
        return preference.getString(key, "123456790")
    }


    fun setWithDrawOpenTime(openTime: String?) {
        editor.putString(WITHDRAW_OPEN_TIME, openTime).apply()
    }

    fun getWithDrawOpenTime(key: String): String? {
        return preference.getString(key, "")
    }

    fun setWithDrawCloseTime(closeTime: String?) {
        editor.putString(WITHDRAW_CLOSE_TIME, closeTime).apply()
    }

    fun getWithDrawCloseTime(key: String): String? {
        return preference.getString(key, "")
    }

    fun setPaymentMethod(paymentMethod: String?) {
        editor.putString(PAYMENT_METHOD, paymentMethod).apply()
    }

    fun getPaymentMethod(key: String): String? {
        return preference.getString(key, "")
    }

    fun setAutoResultApi(autoResult: String?) {
        editor.putString(AUTO_RESULT_API, autoResult).apply()
    }

    fun getAutoResultApi(key: String): String? {
        return preference.getString(key, "")
    }

    fun setSmsAPiKey(autoResult: String?) {
        editor.putString(SMS_API_KEY, autoResult).apply()
    }

    fun getSmsAPiKey(key: String): String? {
        return preference.getString(key, "")
    }

    fun setBankWithDrawEnable(enable: Int?) {
        enable?.let { editor.putInt(BANK_WITHDRAW_ENABLE, it).apply() }
    }

    fun getBankWithDrawEnable(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setUpiWithDrawEnable(enable: Int?) {
        enable?.let { editor.putInt(UPI_WITHDRAW_ENABLE, it).apply() }
    }

    fun getUpiWithDrawEnable(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setFcmKey(token: String?) {
        editor.putString(FCM_KEY, token).apply()
    }

    fun getFcmKey(key: String): String? {
        return preference.getString(key, null)
    }


    fun setID(id: Int?) {
        id?.let { editor.putInt(ID, it).apply() }
    }

    fun getID(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setUserID(id: Int?) {
        id?.let { editor.putInt(USER_ID, it).apply() }
    }

    fun getUserID(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setName(name: String?) {
        editor.putString(NAME, name).apply()
    }

    fun setmPillers(name: Int) {
        editor.putInt(PILLERS, name).apply()
    }

    fun getmPillers(key: String): Int {
        return preference.getInt(key, 0)
    }

    fun getName(key: String): String? {
        return preference.getString(key, null)
    }

    fun setPhone(phone: String?) {
        phone?.let { editor.putString(PHONE, phone).apply() }
    }

    fun getPhone(key: String?): String? {
        return preference.getString(key, null)
    }

    fun setBalance(balance: Double?) {
        balance?.let { editor.putFloat(BALANCE, it.toFloat()).apply() }
    }

    fun getBalance(key: String?): Double {
//        return preference.getInt(key, 0)
        val raw = preference.getFloat(key, 0.0f).toDouble()
        return String.format("%.2f", raw).toDouble()
    }

    fun setGeneralNotification(notification: Int?) {
        notification?.let { editor.putInt(GENERAL_NOTIFICATION, it).apply() }
    }

    fun getGeneralNotification(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setStartLineNotification(notification: Int?) {
        notification?.let { editor.putInt(START_LINE_NOTIFICATION, it).apply() }
    }

    fun getStartLineNotification(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setDesawarNotification(notification: Int?) {
        notification?.let { editor.putInt(DESAWAR_NOTIFICATION, it).apply() }
    }

    fun getDesawarNotification(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setOwnCode(ownCode: Int?) {
        ownCode?.let { editor.putInt(OWN_CODE, it).apply() }
    }

    fun getOwnCode(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setBonus(bonus: Int?) {
        bonus?.let { editor.putInt(BONUS, it).apply() }
    }

    fun getBonus(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setBlocked(blocked: Int?) {
        blocked?.let { editor.putInt(BLOCKED, it).apply() }
    }

    fun getBlocked(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setRole(role: String?) {
        editor.putString(ROLE, role).apply()
    }

    fun getRole(key: String): String? {
        return preference.getString(key, null)
    }

    fun setAppUpdateLink(link: String?) {
        editor.putString(APP_UPDATE_LINK, link).apply()
    }

    fun getAppUpdateLink(key: String): String? {
        return preference.getString(key, null)
    }

    fun setHomePageImageUrl(link: String?) {
        editor.putString(IMAGE_URL, link).apply()
    }

    fun getHomePageImageUrl(key: String): String? {
        return preference.getString(key, null)
    }


    fun setConfirmed(confirmed: Int?) {
        confirmed?.let { editor.putInt(CONFIRMED, it).apply() }
    }

    fun getConfirmed(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setEnableDesawar(confirmed: Int?) {
        confirmed?.let { editor.putInt(Constants.ENABLE_DESAWAR, it).apply() }
    }
    fun setEnableDesawarOlny(confirmed: Int?) {
        confirmed?.let { editor.putInt(ENABLE_DESAWAR_ONLY, it).apply() }
    }
    fun setShowResultsOnly(confirmed: Int?) {
        confirmed?.let { editor.putInt(SHOW_RESULTS_ONLY, it).apply() }
    }

    fun getEnableDesawar(key: String?): Int {
        return preference.getInt(key, 0)
    }
    fun getShowResultsOnly(): Int {
        return preference.getInt(SHOW_RESULTS_ONLY, 0)
    }
    fun getEnableDesawarOnly(): Int {
        return preference.getInt(ENABLE_DESAWAR_ONLY, 0)
    }

    fun setMainTainMode(confirmed: Int?) {
        confirmed?.let { editor.putInt(MAINTAIN_MODE, it).apply() }
    }

    fun getPlayStoreEnable(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setPlayStoreEnable(confirmed: Int?) {
        confirmed?.let { editor.putInt(PLAY_STORE_ENABLE, it).apply() }
    }

    fun getMainTainMode(key: String?): Int {
        return preference.getInt(key, 0)
    }

    fun setVersion(confirmed: Int?) {
        confirmed?.let { editor.putInt(VERSION, it).apply() }
    }

    fun getVersion(key: String?): Int {
        return preference.getInt(key, 1)
    }

    fun setIsUserLogin(isUserLogin: Boolean?) {
        isUserLogin?.let { editor.putBoolean(IS_USER_LOGIN, it).apply() }
    }

    fun setSessionOutStatus(isSessionOut: Boolean) {
        editor.putBoolean("sessionStatus", isSessionOut).apply()
    }

    fun getSessionOutStatus():Boolean {
       return preference.getBoolean("sessionStatus", false)
    }

    fun getIsUserLogin(key: String?): Boolean {
        return preference.getBoolean(key, false)
    }

    fun getIsUserLoginWithPin(key: String?): Boolean {
        return preference.getBoolean(key, false)
    }

    fun setPinLock(key: String, isEnable: Boolean) {
        editor.putBoolean(key, isEnable).apply()
    }


    fun setAccountHolderName(name: String?) {
        editor.putString(ACCOUNT_HOLDER_NAME, name).apply()
    }

    fun getAccountHolderName(key: String): String? {
        return preference.getString(key, "")
    }

    fun setAccountNumber(name: String?) {
        editor.putString(ACCOUNT_NUMBER, name).apply()
    }

    fun getAccountNumber(key: String): String? {
        return preference.getString(key, "")
    }

    fun setIFSCode(name: String?) {
        editor.putString(IFSC_CODE, name).apply()
    }

    fun getIFSCode(key: String): String? {
        return preference.getString(key, "")
    }

    fun setUserUpi(name: String?) {
        editor.putString(USER_UPI, name).apply()
    }

    fun getUserUpi(key: String): String? {
        return preference.getString(key, "")
    }

    fun setSliderUrl(name: String?) {
        editor.putString(SLIDER, name).apply()
    }

    fun getSliderUrl(key: String): String? {
        return preference.getString(key, "")
    }

    fun setEarningSystem(isEarningSystem: Boolean) {
        editor.putBoolean(Constants.EARNING_SYSTEM, isEarningSystem).apply()
    }

    fun getEarningSystem(): Boolean {
        return preference.getBoolean(Constants.EARNING_SYSTEM, false)
    }

    fun setMatkaEnable(enable: Boolean) {
        editor.putBoolean(Constants.MATKA_ENABLE, enable).apply()
    }

    fun getMatkaEnable(): Boolean {
        return preference.getBoolean(Constants.MATKA_ENABLE, false)
    }
}