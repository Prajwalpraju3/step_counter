import android.app.Service
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            running  =true
            count = Constant.getSharePref(this).getFloat(STEP_COUNT_TODAY, 0f).toInt()
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
        } catch (e: Exception) {
            Log.e("eee ERROR", e.message.toString())
        }


        return START_STICKY
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSensorChanged(event: SensorEvent?) {
        if (running)
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
        val intent = Intent(this,MyPhoneReciver::class.java)
        sendBroadcast(intent)
        super.onDestroy()
    }

}