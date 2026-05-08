package com.userplay.bazar22.network


import com.userplay.bazar22.models.PayUrlIntentResponse
import com.userplay.bazar22.models.RedeemGiftResponse
import com.userplay.bazar22.models.SendBody
import com.userplay.bazar22.models.SendBodyTotal
import com.userplay.bazar22.models.UpiMoneyResponse
import com.userplay.bazar22.models.forgot_otp.ForgotOtpResponse
import com.userplay.bazar22.models.forgot_otp_verify.ForgotOtpVerifyResponse
import com.userplay.bazar22.models.game_submit.GameSubmitResponse
import com.userplay.bazar22.models.getWithDrawHistory.GetWithDrawHistroyResponse
import com.userplay.bazar22.models.get_app_data.newpackage.AppDataResponse.AppDataResponse
import com.userplay.bazar22.models.get_deposit_history.GetDepositHistoryResponse
import com.userplay.bazar22.models.get_game_history.GetGameHistoryResponse
import com.userplay.bazar22.models.get_markets.GeneralMarketResponse
import com.userplay.bazar22.models.get_rate_new.GameRateResponseNew
import com.userplay.bazar22.models.get_result.GetResultResponse
import com.userplay.bazar22.models.getpassbook.GetPassbookResponse
import com.userplay.bazar22.models.login.LoginResponse
import com.userplay.bazar22.models.notification.NotificationChangeResponse
import com.userplay.bazar22.models.pay_url.PayUrlResponse
import com.userplay.bazar22.models.payment_added.PaymentAddedResponse
import com.userplay.bazar22.models.refferal.RefferalResponse
import com.userplay.bazar22.models.user_level.UserLevelResponse
import com.userplay.bazar22.models.save_bank_details.SaveBankDetails
import com.userplay.bazar22.models.saveupi.SaveUpiResponse
import com.userplay.bazar22.models.send_signup_otp.SignUpOtpResponse
import com.userplay.bazar22.models.signup.SignUpResponse
import com.userplay.bazar22.models.verify_signup_otp.VerifySignUpOtpResponse
import com.userplay.bazar22.models.withdraw_balance.WithDrawBalanceResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("api/get-app-data")
    suspend fun getAppData(): Response<AppDataResponse>

    @FormUrlEncoded
    @POST("api/login")
    suspend fun getLogin(
        @Field("phone") phone: String?,
        @Field("mpin") mPin: Int?,
        @Field("fcm") fcm: String?
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("api/signup")
    suspend fun getSignup(
        @Field("phone") phone: String?,
        @Field("password") password: String?,
        @Field("fcm") fcm: String?,
        @Field("name") userName: String?,
        @Field("refferal_code") referralCode: String?,
        @Field("agentCode") agentCode: String?
    ): Response<SignUpResponse>

    @FormUrlEncoded
    @POST("api/upi-payment-url")
    suspend fun getPayUrl(
        @Field("amount") amount: Int?,
    ): Response<PayUrlResponse>

    @FormUrlEncoded
    @POST("api/pay-from-upi-payment-url")
    suspend fun getPayFromUpiUrl(
        @Field("amount") amount: Int?,
    ): Response<PayUrlResponse>


    @FormUrlEncoded
    @POST("api/ibr-pay-upi-payment-url")
    suspend fun getPayUrlIntent(
        @Field("amount") amount: Int?,
    ): Response<PayUrlIntentResponse>

    @FormUrlEncoded
    @POST("api/add-payment")
    suspend fun addPayment(
        @Field("amount") amount: Int?,
        @Field("pay_status") pay_status: String?,
    ): Response<PaymentAddedResponse>

    @FormUrlEncoded
    @POST("api/send-signup-otp")
    suspend fun sendSignUpOtp(
        @Field("phone") phone: String?
    ): Response<SignUpOtpResponse>

    @FormUrlEncoded
    @POST("api/verify-signup-otp")
    suspend fun verifySignUpOtp(
        @Field("otp") otp: Int?,
        @Field("phone") phone: String?
    ): Response<VerifySignUpOtpResponse>


    @FormUrlEncoded
    @POST("api/send-forget-password-otp")
    suspend fun forgotOtp(
        @Field("phone") phone: String?
    ): Response<ForgotOtpResponse>

    @FormUrlEncoded
    @POST("api/verify-forget-password-otp")
    suspend fun verifyForgotOtp(
        @Field("phone") phone: String?,
        @Field("mpin") mpin: String?,
        @Field("otp") otp: String?
    ): Response<ForgotOtpVerifyResponse>

    @FormUrlEncoded
    @POST("api/get-markets")
    suspend fun getMarket(
        @Field("type") type: String
    ): Response<GeneralMarketResponse>

    @FormUrlEncoded
    @POST("api/get-game-history")
    suspend fun getGameHistory(
        @Field("type") type: String,
        @Field("page") page: Int
    ): Response<GetGameHistoryResponse>

    @FormUrlEncoded
    @POST("api/get-transactions")
    suspend fun getPassBook(
        @Field("page") page: Int
    ): Response<GetPassbookResponse>

    @FormUrlEncoded
    @POST("api/save-upi-details")
    suspend fun saveUpiDetails(
        @Field("upi_name") upiName: String,
        @Field("upi_id") upiId: String
    ): Response<SaveUpiResponse>

    @FormUrlEncoded
    @POST("api/save-bank-details")
    suspend fun saveBankDetails(
        @Field("account_holder_name") name: String,
        @Field("account_number") number: String,
        @Field("account_ifsc_code") ifsc: String
    ): Response<SaveBankDetails>


    @FormUrlEncoded
    @POST("api/get-deposit-history")
    suspend fun getDepositHistory(
        @Field("page") page: Int
    ): Response<GetDepositHistoryResponse>


    @FormUrlEncoded
    @POST("api/get-withdrawl-history")
    suspend fun getWithDrawHistory(
        @Field("page") page: Int
    ): Response<GetWithDrawHistroyResponse>


    @FormUrlEncoded
    @POST("api/withdraw-balance")
    suspend fun getWithDrawBalance(
        @Field("amount") amount: String,
        @Field("withdraw_mode") withdrawMode: String
    ): Response<WithDrawBalanceResponse>

    @FormUrlEncoded
    @POST("api/change-notification")
    suspend fun changeNotification(@Field("type") type: String?): Response<NotificationChangeResponse>

    @POST("api/submit-game")
    suspend fun submitGame(@Body request: SendBody): Response<GameSubmitResponse>

    @POST("api/submit-game")
    suspend fun submitGameTotal(@Body request: SendBodyTotal): Response<GameSubmitResponse>

    @GET("api/get-game-rates")
    suspend fun getGameRates(): Response<GameRateResponseNew>

    @GET("api/get-referral-details")
    suspend fun getReferralDetails(): Response<RefferalResponse>

    @GET("api/get-user-level")
    suspend fun getUserLevels(
        @Query("level_id") levelId: Int?
    ): Response<UserLevelResponse>

    @GET("api/get-game-results")
    suspend fun getGamesResult(
        @Query("type") type: String,
        @Query("date") date: String,
    ): Response<GetResultResponse>

    @FormUrlEncoded
    @POST("api/upi-money-upi-payment-url")
    suspend fun upiMoneyPaymentUrl(
        @Field("amount") amount: Int?,
    ): Response<UpiMoneyResponse>

    @FormUrlEncoded
    @POST("api/redeem-code")
    suspend fun redeemGift(
        @Field("gift_code") giftCode: String,
    ): Response<RedeemGiftResponse>
}