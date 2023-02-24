package com.example.stepcountpoc

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_DETECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stepcountpoc.database.AppDatabase
import com.example.stepcountpoc.databinding.ActivityMainBinding
import com.example.stepcountpoc.sevices.MyService
import com.ix.ibrahim7.stepcounter.other.STEP_COUNT_TARGET
import com.ix.ibrahim7.stepcounter.other.STEP_COUNT_TODAY
import com.ix.ibrahim7.stepcounter.util.Constant


class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepDetectorSensor: Sensor? = null
    private var running = false
    var count = 0
    private lateinit var appDb: AppDatabase


    //    private var totalStep = 0f
//    private var previousTotalStep = 0f
    private var todaysTotalSteps = 0


    private val PHYISCAL_ACTIVITY = 23

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)

        // Adding a context of SENSOR_SERVICE as Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

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
            } else {
                initializeSensors()
                addListenerAndResetValues()
            }
        } else {
            initializeSensors()
            addListenerAndResetValues()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.steps_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.clear_steps -> {
                setCountToZero()
                Toast.makeText(this, "Success..!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.change_target -> {
                openDialog()


                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDialog() {
        var dialog = Dialog(this, android.R.style.Theme_Material_Light_Dialog_Alert)
        dialog.setContentView(R.layout.simple_input)
        val btOk = dialog.findViewById(R.id.bt_ok) as Button
        var etSteps = dialog.findViewById(R.id.et_steps) as EditText
        val goalCount =  Constant.getSharePref(this).getFloat(STEP_COUNT_TARGET, 5000f).toInt()
        etSteps.setText("$goalCount")

        btOk.setOnClickListener {
            if (etSteps.text.isNotEmpty()) {
                Constant.editor(this).putFloat(STEP_COUNT_TARGET, etSteps.text.toString().toFloat())
                    .apply()
                dialog.dismiss()
                Toast.makeText(
                    this,
                    "New Target ${etSteps.text} Updated!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                dialog.dismiss()
            }
        }
        dialog.show()


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PHYISCAL_ACTIVITY) {
            initializeSensors()
            addListenerAndResetValues()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(applicationContext, MyService::class.java))
        }
    }

    private fun initializeSensors() {
        running = true
        stepDetectorSensor = sensorManager?.getDefaultSensor(TYPE_STEP_DETECTOR)
        if (stepDetectorSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
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
            if (event?.sensor?.type == TYPE_STEP_DETECTOR) {
                count += 1
                todaysTotalSteps += 1
                binding.tvStepsTaken.text = count.toString()
                binding.tvTotalStepsValue.text = todaysTotalSteps.toInt().toString()
                binding.progressBar.visibility = View.GONE
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
        loadData()
    }


    private fun saveData() {
        Constant.editor(this).putFloat(STEP_COUNT_TODAY, todaysTotalSteps.toFloat()).apply()
    }


    private fun setCountToZero() {
        todaysTotalSteps = 0
        count = 0
        Constant.editor(this).putFloat(STEP_COUNT_TODAY, todaysTotalSteps.toFloat()).apply()
        binding.tvTotalStepsValue.text = todaysTotalSteps.toString()
        binding.tvStepsTaken.text = count.toString()
        startMyService()

    }

    private fun loadData() {
        todaysTotalSteps = Constant.getSharePref(this).getFloat(STEP_COUNT_TODAY, 0f).toInt()
        binding.tvTotalStepsValue.text = todaysTotalSteps.toInt().toString()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        saveData()
        startMyService()
        super.onStop()
    }

    private fun startMyService() {
        if (isMyServiceRunning(MyService::class.java)) stopService(
            Intent(
                applicationContext,
                MyService::class.java
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, Intent(this, MyService::class.java))
        } else {
            this.startService(Intent(this, MyService::class.java));
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        if (stepDetectorSensor != null) {
            sensorManager?.unregisterListener(this, stepDetectorSensor)
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }


    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


}