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
import com.ix.ibrahim7.stepcounter.other.STEP_COUNT_TARGET
import com.ix.ibrahim7.stepcounter.other.STEP_COUNT_TODAY
import com.ix.ibrahim7.stepcounter.util.Constant


class MyService : Service(),SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false

    var count = 0
    var target = 0
    lateinit var notification: Notification.Builder

    lateinit var notificationManager: NotificationManager
    private var stepDetectorSensor:Sensor? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        running  =true
        count = Constant.getSharePref(this).getFloat(STEP_COUNT_TODAY, 0f).toInt()
        target = Constant.getSharePref(this).getFloat(STEP_COUNT_TARGET, 5000f).toInt()

        sensorManager =  getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR,false)

//      if(stepDetectorSensor==null) {
//          return
//      }


        val notificationIntent = Intent(applicationContext, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            202,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )


        val CHANNELID = "Foreground Service ID"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_HIGH
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        notification = Notification.Builder(this, CHANNELID)
            .setContentText("Try to complete your target!!")
            .setContentIntent(pendingIntent)
            .setContentTitle("Steps $count / $target")
            .setSmallIcon(com.example.stepcountpoc.R.drawable.ic_run)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(1001, notification.build())

        sensorManager?.registerListener(
            this,
            stepDetectorSensor,
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )


        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Log.e("MyService", "onStartCommand")
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
        notification.setContentTitle("Steps $count / $target");
        notificationManager.notify(1001, notification.build());
        Constant.editor(this).putFloat(STEP_COUNT_TODAY,count.toFloat()).apply()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e("MyService", "onBind")

        return null
    }

    override fun stopService(name: Intent?): Boolean {
        Log.e("MyService", "stopService")

        return super.stopService(name)
    }

    override fun onDestroy() {
        Log.e("MyService", "onDestroy")
        if (stepDetectorSensor != null) {
            sensorManager?.unregisterListener(this, stepDetectorSensor)
        }
        val intent = Intent(this,MyPhoneReciver::class.java)
        sendBroadcast(intent)
        super.onDestroy()
    }

}