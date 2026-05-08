package com.userplay.bazar22

import android.app.Application
import android.util.Log
import com.userplay.bazar22.network.CheckNetwork
import com.userplay.bazar22.preferences.MatkaPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch
import javax.inject.Inject

@HiltAndroidApp
class MatkaApplication : Application() {

    @Inject
    lateinit var mPref: MatkaPref

    override fun onCreate() {
        super.onCreate()
        CheckNetwork(this).registerNetworkCallback()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("firebase_error", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            mPref.setFcmKey(token)
            Log.e("firebase_token", "" + token)
        })

        Branch.enableLogging() // Optional: Enable for debugging
        Branch.getAutoInstance(this) // Initialize Branch SDK globally

    }
}