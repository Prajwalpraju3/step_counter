package com.example.stepcountpoc.sevices

import android.app.*
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
import com.example.stepcountpoc.MainActivity
import com.ix.ibrahim7.stepcounter.other.STEP_COUNT_TODAY
import com.ix.ibrahim7.stepcounter.util.Constant


class MyService : Service(),SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false

    var count = 0
    lateinit var notification: Notification.Builder

    lateinit var notificationManager: NotificationManager

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

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            202,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val CHANNELID = "Foreground Service ID"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        notification = Notification.Builder(this, CHANNELID)
            .setContentText("Try to complete your target!!")
            .setContentIntent(pendingIntent)
            .setContentTitle("Steps $count / 10000")
            .setSmallIcon(com.example.stepcountpoc.R.drawable.ic_run)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(1001, notification.build())

        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Log.e("com.example.stepcountpoc.sevices.MyService", "onStartCommand")
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
        notification.setContentTitle("Steps $count / 10000");
        notificationManager.notify(1001, notification.build());
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