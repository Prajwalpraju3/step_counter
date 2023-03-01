package com.example.stepcountpoc.sevices
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat

class MyPhoneReciver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context!!, Intent(context, StepDetectorService::class.java))
        } else {
            context?.startService( Intent(context, StepDetectorService::class.java) );
        }    }
}