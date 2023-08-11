package com.example.myapplication

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

private const val TAG = "MainActivity"

// https://developer.android.com/jetpack/compose/tutorial
// https://developer.android.com/training/data-storage/room
// https://developer.android.com/guide/navigation
// https://developer.android.com/topic/architecture/data-layer/offline-first
// https://developer.android.com/guide/topics/connectivity/wifi-aware
// https://developer.android.com/guide/topics/connectivity/wifip2p
// https://developer.android.com/training/connect-devices-wirelessly/nsd-wifi-direct
// https://developers.google.com/nearby/connections/overview
// https://developer.android.com/jetpack/compose/state-saving
// https://developer.android.com/jetpack/compose/layouts/material
// https://developer.android.com/training/data-storage
// https://www.wi-fi.org/discover-wi-fi/wi-fi-aware
// faq What is the relationship between Wi-Fi Aware and Wi-Fi Direct?
// Wi-Fi Aware is a similar peer-to-peer connectivity technology to Wi-Fi Direct. However, while Wi-Fi Direct requires a centralized coordinator, called a Group Owner, Wi-Fi Aware creates decentralized, dynamic peer-to-peer connections. Many applications, such as Miracast and direct printer connections, work well with Wi-Fi Direct. Wi-Fi Aware is positioned to provide peer-to-peer connectivity in highly mobile environments, where devices join or leave in a less deterministic manner. Whether it's professionals at a crowded conference to find each other or strangers on a subway momentarily joining a multi-player game, Wi-Fi Aware connections seamlessly adapt to changing environment and usage conditions.
class MainActivity : ComponentActivity() {
    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    private var receiver: BroadcastReceiver? = null

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (manager == null) {
            Log.i(TAG, "Wifi P2P service not supported")
        }

        manager?.let {
            val c = it.initialize(this, mainLooper, null)
            receiver = WiFiDirectBroadcastReceiver(it, c, this)
        }

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SupportsBluetoothLE()
                }
            }
        }
    }

    /* register the broadcast receiver with the intent values to be matched */
    override fun onResume() {
        super.onResume()
        receiver?.also { receiver ->
            registerReceiver(receiver, intentFilter)
        }
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        receiver?.also { receiver ->
            unregisterReceiver(receiver)
        }
    }

}

@Preview
@Composable
fun SupportsWifiAware() {
    val supportsWifiAware =
        LocalContext.current.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
    if (supportsWifiAware)
        Text("supports wifi aware")
    else
        Text("doesnt support wifi aware")
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun SupportsBluetoothLE() {
    val isInspection = LocalInspectionMode.current
    val bluetoothPermissionsState = if (isInspection) {
        object : MultiplePermissionsState {
            override val allPermissionsGranted: Boolean = false
            override val permissions: List<PermissionState> = emptyList()
            override val revokedPermissions: List<PermissionState> = emptyList()
            override val shouldShowRationale: Boolean = false
            override fun launchMultiplePermissionRequest() {}
        }
    } else {
        rememberMultiplePermissionsState(
            listOf(
                BLUETOOTH_SCAN,
                BLUETOOTH_CONNECT
            )
        )
    }

    if (bluetoothPermissionsState.allPermissionsGranted) {
        Text("Thanks! I can access your exact location :D")
    } else {
        Column {
            val allPermissionsRevoked =
                bluetoothPermissionsState.permissions.size ==
                        bluetoothPermissionsState.revokedPermissions.size

            val textToShow = if (!allPermissionsRevoked) {
                // If not all the permissions are revoked, it's because the user accepted the COARSE
                // location permission, but not the FINE one.
                "Yay! Thanks for letting me access your approximate location. " +
                        "But you know what would be great? If you allow me to know where you " +
                        "exactly are. Thank you!"
            } else if (bluetoothPermissionsState.shouldShowRationale) {
                // Both location permissions have been denied
                "Getting your exact location is important for this app. " +
                        "Please grant us fine location. Thank you :D"
            } else {
                // First time the user sees this feature or the user doesn't want to be asked again
                "This feature requires location permission. Enable permission in app settings if you want it."
            }

            val buttonText = if (!allPermissionsRevoked) {
                "Allow precise location"
            } else {
                "Request permissions"
            }

            Text(text = textToShow)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { bluetoothPermissionsState.launchMultiplePermissionRequest() }) {
                Text(buttonText)
            }
        }
    }
    /*
        val bluetoothLEAvailable =
            LocalContext.current.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        val bluetoothManager: BluetoothManager =
            LocalContext.current.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        /*if (bluetoothAdapter?.isEnabled == false) {
            LocalContext.current.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }*/
        val result = remember { mutableStateOf<ActivityResult?>(null) }
        //val bluetoothPermissionResult = remember { mutableStateOf<Map<String, Boolean>?>(null) }

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result.value = it
            }
        val requestBluetoothPermissions =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                launcher.launch(enableBtIntent)
            }
        Column {
            Button(onClick = {
                requestBluetoothPermissions.launch(
                    arrayOf(
                        BLUETOOTH_SCAN,
                        BLUETOOTH_CONNECT
                    )
                )
            }) {
                Text(text = "Take a picture")
            }
            if (bluetoothLEAvailable)
                Text("supports bluetooth le")
            else
                Text("doesnt support bluetooth le")
            result.value?.let { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Text("bluetooth activated")
                } else {
                    Text("user denied to activate bluetooth")
                }
            }
        }*/

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}