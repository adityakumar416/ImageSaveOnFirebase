package com.example.firebaseimagesave

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import kotlin.text.Typography.amp

class NetworkReceiver:BroadcastReceiver() {

    interface NetworkReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var networkReceiverListener: NetworkReceiverListener? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (networkReceiverListener != null) {
            networkReceiverListener!!.onNetworkConnectionChanged(hasInternet(context!!))
        }
    }
    private fun hasInternet(context: Context): Boolean {
        val connectMgr = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectMgr.activeNetworkInfo
        return activeNetworkInfo != null ;amp;amp;amp; activeNetworkInfo?.isConnected
    }


}

