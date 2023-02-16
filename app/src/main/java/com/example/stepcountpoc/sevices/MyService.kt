package com.example.stepcountpoc.sevices

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.ix.ibrahim7.stepcounter.other.STEP_COUNT_TODAY
import com.ix.ibrahim7.stepcounter.util.Constant


class MyService : Service(),SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false

    var count = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        running  =true
        count = Constant.getSharePref(this).getFloat(STEP_COUNT_TODAY, 0f).toInt()

        sensorManager =  getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        sensorManager?.registerListener(
            this,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        sensorManager?.registerListener(
            this,
            stepDetectorSensor,
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )


        val CHANNELID = "Foreground Service ID"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(this, CHANNELID)
            .setContentText("Service is running")
            .setContentTitle("Service enabled")
            .setSmallIcon(com.example.stepcountpoc.R.drawable.ic_launcher_background)


//            startService(intent)

        startForeground(1001, notification.build())

        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        try {
            Log.e("com.example.stepcountpoc.sevices.MyService", "onStartCommand")


//
//            val CHANNELID = "Foreground Service ID"
//            val channel = NotificationChannel(
//                CHANNELID,
//                CHANNELID,
//                NotificationManager.IMPORTANCE_LOW
//            )
//
//            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
//            val notification: Notification.Builder = Notification.Builder(this, CHANNELID)
//                .setContentText("Service is running")
//                .setContentTitle("Service enabled")
//                .setSmallIcon(com.example.stepcountpoc.R.drawable.ic_launcher_background)
//
//
////            startService(intent)
//
//            startForeground(1001, notification.build())
//            startService(this)
        } catch (e: Exception) {
            Log.e("eee ERROR", e.message.toString())
        }


        return START_STICKY
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.e("MyService", "onSensorChanged")
        count += 1
        Constant.editor(this).putFloat(STEP_COUNT_TODAY,count.toFloat()).apply()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
//        val intent = Intent(this, com.example.stepcountpoc.sevices.MyPhoneReciver::class.java)
//        sendBroadcast(intent)
        super.onDestroy()
    }

}