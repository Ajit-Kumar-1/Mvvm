package com.example.mvvm.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
class ConnectivityReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        connectivityReceiverListener?.
            onNetworkConnectionChanged(isConnectedOrConnecting(context!!))
    }
    private fun isConnectedOrConnecting(context: Context): Boolean {
        val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager).activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }
    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}