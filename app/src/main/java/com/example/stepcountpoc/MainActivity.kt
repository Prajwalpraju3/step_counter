package com.example.stepcountpoc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_COUNTER
import android.hardware.Sensor.TYPE_STEP_DETECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.stepcountpoc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var running = false
    var count = 0

    private val PHYISCAL_ACTIVITY = 23

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    PHYISCAL_ACTIVITY
                )
            };
        } else {
            addListenerAndResetValues()
        }


        // Adding a context of SENSOR_SERVICE as Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PHYISCAL_ACTIVITY) {
            addListenerAndResetValues()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        initializeSensors()
        addListenerAndResetValues()
    }

    private fun initializeSensors() {
        running = true
        stepCounterSensor = sensorManager?.getDefaultSensor(TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager?.getDefaultSensor(TYPE_STEP_DETECTOR)
        if (stepCounterSensor == null || stepDetectorSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            sensorManager?.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH
            )
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
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            if (event?.sensor?.type == TYPE_STEP_COUNTER) {
                binding.tvTotalStepsValue.text = event.values[0].toInt().toString()
                binding.progressBar.visibility = View.GONE
            } else if (event?.sensor?.type == TYPE_STEP_DETECTOR) {
                count += event.values[0].toInt()
                binding.tvStepsTaken.text = count.toString()
                binding.progressBar.visibility = View.VISIBLE

            }
        }

    }

    private fun addListenerAndResetValues() {

        binding.tvStepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        binding.tvStepsTaken.setOnLongClickListener {
            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            binding.tvStepsTaken.text = 0.toString()
            count = 0
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (stepCounterSensor != null) {
            sensorManager?.unregisterListener(this, stepCounterSensor)
        }
        if (stepDetectorSensor != null) {
            sensorManager?.unregisterListener(this, stepDetectorSensor)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }
}