package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf

var isBluetoothEnabled = mutableStateOf(BluetoothState.OFF)

enum class BluetoothState {
    OFF, TURNING_ON, ON, TURNING_OFF
}

fun toBluetoothState(state: Int): BluetoothState {
    return when (state) {
        BluetoothAdapter.STATE_OFF -> {
            BluetoothState.OFF
        }

        BluetoothAdapter.STATE_TURNING_ON -> {
            BluetoothState.TURNING_ON
        }


        BluetoothAdapter.STATE_ON -> {
            BluetoothState.ON
        }

        BluetoothAdapter.STATE_TURNING_OFF -> {
            BluetoothState.TURNING_OFF
        }

        else -> {
            throw IllegalStateException("unknown bluetooth state")
        }
    }
}

class BluetoothBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            isBluetoothEnabled.value = toBluetoothState(
                intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
            )
        }
    }
}