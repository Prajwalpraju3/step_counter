package com.ix.ibrahim7.stepcounter.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.stepcountpoc.R
import com.ix.ibrahim7.stepcounter.other.SHARE
import com.ix.ibrahim7.stepcounter.other.TAG

object Constant {

    fun getSharePref(context: Context) =
        context.getSharedPreferences(SHARE, Activity.MODE_PRIVATE)

    fun editor(context: Context) = getSharePref(context).edit()



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setUpStatusBar(activity: Activity) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.purple_200)
    }


    @SuppressLint("LogNotTimber")
    fun printLog(tag:String, message:String?){
        Log.v("$TAG $tag", message!!)
    }



}