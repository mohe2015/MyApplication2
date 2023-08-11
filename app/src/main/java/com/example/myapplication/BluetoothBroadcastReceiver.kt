package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf

var isBluetoothEnabled = mutableStateOf(false)

class BluetoothBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> {
                    isBluetoothEnabled.value = false
                    Log.i("Bluetooth", "State OFF")
                }

                BluetoothAdapter.STATE_ON -> {
                    isBluetoothEnabled.value = true
                    Log.i("Bluetooth", "State ON")
                }
            }

        }
    }
}