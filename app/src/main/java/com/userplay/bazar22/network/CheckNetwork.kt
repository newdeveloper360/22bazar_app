package com.userplay.bazar22.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Build


class CheckNetwork(var context: Context) {

    companion object {
        var isNetworkConnected = false
    }

    // Network Check
    fun registerNetworkCallback() {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        isNetworkConnected = true // Global Static Variable
                    }

                    override fun onLost(network: Network) {
                        isNetworkConnected = false // Global Static Variable
                    }
                }
                )
            }
            isNetworkConnected = false
        } catch (e: Exception) {
            isNetworkConnected = false
        }
    }

}