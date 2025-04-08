package com.example.zhdyn


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timeReceiver: BroadcastReceiver
    private var minutesPassed = 0
    private var isWaiting = true
    private lateinit var statusText: TextView

    private val toastText = "Ждун покинул чат..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        val stopButton: Button = findViewById(R.id.stopButton)
        
        stopButton.setOnClickListener {
            if (isWaiting) {
                unregisterReceiver(timeReceiver)
                isWaiting = false
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            }
        }

        val timeFilter = IntentFilter(Intent.ACTION_TIME_TICK)
        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                minutesPassed++
                statusText.text = "время созерцания: $minutesPassed мин."
            }
        }
        registerReceiver(timeReceiver, timeFilter)

        val batteryLowReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                statusText.text = "накормите Ждуна, силы на исходе!"
            }
        }
        registerReceiver(batteryLowReceiver, IntentFilter(Intent.ACTION_BATTERY_LOW))

        val chargingReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                if (!isCharging) {
                    statusText.text = "накормите Ждуна, силы на исходе!"
                }
            }
        }
        registerReceiver(chargingReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isWaiting) {
            unregisterReceiver(timeReceiver)
        }
    }
}
