package com.userplay.bazar22.di


import android.content.Context
import android.util.Log
import com.example.rfid.demo.di.LiveUrl
import com.userplay.bazar22.network.ApiInterface
import com.userplay.bazar22.preferences.MatkaPref
import com.userplay.bazar22.utils.Constants.LIVE_SERVER
import com.userplay.bazar22.utils.Constants.TOKEN
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext ctx: Context): MatkaPref {
        return MatkaPref(ctx)
    }

    @Singleton
    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun providesHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        preference: MatkaPref
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor { chain ->
            var request = chain.request()
            val reqBuilder = request.newBuilder()
            reqBuilder.addHeader("Accept", "application/json")
//            reqBuilder.addHeader("Acc", "application/json")
            if (preference.getToken(TOKEN) != null) {
                reqBuilder.header(
                    "Authorization",
                    "Bearer " + preference.getToken(TOKEN)
                )
                Log.e("mecalled","token"+preference.getToken(TOKEN))
            }
//                    .addHeader("source", "ANDROID")
            request = reqBuilder.build()
            chain.proceed(request)
        }.readTimeout(90, TimeUnit.SECONDS).connectTimeout(90, TimeUnit.SECONDS).build()
    }


    @Singleton
    @Provides
    fun provideGsonFactory(): Gson {
        return GsonBuilder().setLenient().create()
    }


    @Singleton
    @Provides
    fun providesConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }


    @Singleton
    @Provides
    @LiveUrl
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(LIVE_SERVER).client(okHttpClient)
            .addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Singleton
    fun provideApiService(@LiveUrl retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

}