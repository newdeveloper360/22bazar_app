package com.userplay.bazar22.utils

import android.content.Context
import com.userplay.bazar22.R
import com.userplay.bazar22.network.ApiState
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object Constants {

    const val LIVE_SERVER = "https://22bazar.com/"
    const val PFU_SDK_API_KEY="8|vydZYZuXSWEVYJv6CqUMZ96RuprYIv0wpSoNE6bW394d153c"

    //    const val LIVE_SERVER = "https://kalyanmain.site/"
    const val OTP_SYSTEM_REMOVED = "otp_system_removed"
    const val PRODUCTION_APP = true
    const val SHARED_PRE_FILE = "matka_prefMatka777"
    const val IS_USER_LOGIN = "is_user_login"
    const val SHOW_STARLINE = true
    const val GENERAL_MARKET = "general"
    const val DESAWAR_MARKET = "desawar"
    const val STARLINE_MARKET = "startLine"
    const val LIVE_CHAT_URL = "https://tawk.to/chat/664088f607f59932ab3ea39d/1htm1e171"
    const val ENABLE_LIVE_CHAT = false
    var showLiveUsers = false
    var disablePassBook = false

    const val GENERAL = 1
    const val DESAWAR = 2
    const val STARLINE = 3

    const val NULL_GAME_TYPENULL_GAME_TYPE = "null"
    const val TOKEN = "token"
    const val SESSION_TYPE = "session_type"
    const val HOME_MESSAGE = "home_message"
    const val SUPPORT_NUMBER = "support_number"
    const val SUPPORT_TIME = "support_time"
    const val WITHDRAW_CONDITION = "withdrawl_condition"
    const val RULE_NOTICE = "rules_notice"
    const val CHART_URL = "chart_url"
    const val MIN_WITHDRAW = "min_withdraw"
    const val MIN_BID = "min_bid"
    const val MIN_DEPOSIT = "min_deposit"
    const val INVITE_BONUS = "invite_bonus"
    const val INVITE_SYSTEM_ENABLE = "invite_system_enable"
    const val WELCOME_BONUS = "welcome_bonus"
    const val ADMIN_UPI = "admin_upi"
    const val TELEGRAM_ENABLE = "telegram_enable"
    const val TELEGRAM_LINK = "telegram_link"
    const val WHATSAPP_ENABLE = "whatsapp_enable"
    const val WHATSAPP_NUMBER = "whatsapp_number"
    const val WITHDRAW_OPEN_TIME = "withdraw_open_time"
    const val WITHDRAW_CLOSE_TIME = "withdraw_close_time"
    const val PAYMENT_METHOD = "payment_method"
    const val AUTO_RESULT_API = "auto_result_api"
    const val SMS_API_KEY = "sms_api_key"
    const val BANK_WITHDRAW_ENABLE = "bank_withdraw_enable"
    const val UPI_WITHDRAW_ENABLE = "upi_withdraw_enable"
    const val FCM_KEY = "fcm_key"

    const val ACCOUNT_HOLDER_NAME = "account_holder_name"
    const val ACCOUNT_NUMBER = "account_number"
    const val IFSC_CODE = "ifsc_code"
    const val USER_UPI = "user_upi"
    const val SLIDER = "slider"

    const val ID = "id"
    const val USER_ID = "user_id"
    const val NAME = "name"
    const val PILLERS = "pillers"
    const val PHONE = "phone"
    const val BALANCE = "balance"
    const val GENERAL_NOTIFICATION = "general_noti"
    const val START_LINE_NOTIFICATION = "startline_noti"
    const val DESAWAR_NOTIFICATION = "desawar_noti"
    const val OWN_CODE = "own_code"
    const val BONUS = "bonus"
    const val BLOCKED = "blocked"
    const val ROLE = "role"
    const val APP_UPDATE_LINK = "app_update_link"
    const val IMAGE_URL = "image_url"
    const val CONFIRMED = "confirmed"
    const val ENABLE_DESAWAR = "enable_desawar"
    const val ENABLE_DESAWAR_ONLY = "enable_desawar_only"
    const val SHOW_RESULTS_ONLY = "show_results_only"
    const val MAINTAIN_MODE = "maintain_mode"
    const val PLAY_STORE_ENABLE = "play_store_enbale"
    const val VERSION = "version"

    const val OPEN_GAME_TYPE = "open"
    const val CLOSE_GAME_TYPE = "close"
    const val NULL_GAME_TYPE = "null"

    const val SINGLE_GAME_TYPE = "single_game"
    const val SINGLE_BULK_GAME_TYPE = "single_bulk_game"
    const val JODI_GAME_TYPE = "jodi_game"
    const val JODI_BULK_GAME_TYPE = "jodi_bulk_game"


    const val SINGLE_PANA_GAME = "single_pana_game"
    const val DOUBLE_PANA_GAME_TYPE = "double_pana_game"
    const val TRIPLE_PANA_GAME_TYPE = "triple_pana_game"
    const val EARNING_SYSTEM = "earning_system"
    const val MATKA_ENABLE = "MATKA_ENABLE"
//    const val CLOSE_GAME_TYPE = "close"
//    const val CLOSE_GAME_TYPE = "close"

    //dont change this
    var enableJodiBulkDigitsNewDesign = true


    @Volatile
    lateinit var appContext: Context

    private lateinit var mMessage: String

    fun setContext(context: Context) {
        appContext = context
    }

    fun <T> handleResponse(response: Response<T>): ApiState<T> {
        return try {
            when {
                response.message().toString().contains("timeout") -> {
                    ApiState.Error("TimeOut")
                }

                response.body() == null -> {
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    if (response.code() == 400 && jsonObject.has("Message")) {
                        mMessage = jsonObject.getString("Message")
                        ApiState.Error(mMessage)
                    } else {
                        ApiState.Error("Data is null " + response.body())
                    }
                }

                response.isSuccessful -> {
                    if (response.body() != null) {
                        ApiState.Success(response.body()!!)
                    } else {
                        ApiState.Error("Response is null")
                    }
                }

                response.code() != 500 && response.code() != 404 -> {
                    if (response.errorBody() != null) {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        if (jsonObject.has("Message")) {
                            mMessage = jsonObject.getString("Message")
                            ApiState.Error(mMessage)
                        } else {
                            ApiState.Error("Message object is null")
                        }
                    } else {
                        ApiState.Error("Error body is null")
                    }
                }

                else -> {
                    ApiState.Error(response.message())
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            return when (t) {
                is SocketTimeoutException -> {
                    ApiState.Error(appContext.resources.getString(R.string.slow_Internet_connection))
                }

                is UnknownHostException -> {
                    ApiState.Error(appContext.resources.getString(R.string.check_your_internet))
                }

                is JSONException -> {
                    ApiState.Error(t.message)
                }

                is Exception -> {
                    ApiState.Error(t.localizedMessage)
                }

                else -> {
                    ApiState.Error(appContext.resources.getString(R.string.places_try_again))
                }
            }
        }
    }

    var enableSinglePanaNewDesign = true
    var enableSinglePanaBulkNewDesign = true
    var enableDoublePanaNewDesign = true
    var enableDoublePanaBulkNewDesign = true
    var enableTriplePanaNewDesign = true
    var enableSingleDigitsNewDesign = true
    var enableSingleBulkDigitsNewDesign = false
    var enableJodiDigitsNewDesign = true
    var enableJodiGroupNewDesign = true
    var enableJodiTotalNewDesign = true
   /* var enableSPMotorNewDesign = true
    var enableDPMotorNewDesign = true*/
    var enableHalfSangamA = true
    var enableHalfSangamB = true
    var enableFullSangam = true
    var enableHalfSangam = true
    var newPanaPage = true
    var enablePlayDesawar = false
    var isEnableNewJodiFamilyNumber = true
    const val IS_USER_LOGIN_WITH_MPIN = "IS_USER_LOGIN_WITH_MPIN"

    var sessionExpiredDialog = false
}