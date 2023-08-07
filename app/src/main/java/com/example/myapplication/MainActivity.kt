package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

/*
~/Android/Sdk/platform-tools/adb devices -l
~/Android/Sdk/platform-tools/adb -s adb-100000002e70bd0a-yn6lUH._adb-tls-connect._tcp shell
su
ps -A -f | grep wpa

find | grep wpa_supplicant
./vendor/etc/wifi/wpa_supplicant_overlay.conf # read only
./vendor/etc/wifi/wpa_supplicant.conf # read only
./data/vendor/wifi/wpa/wpa_supplicant.conf

wpa_cli
scan
scan_results
save_config
dump
driver_flags
status verbose

get p2p_disabled
save_config
dump
set p2p_disabled 0
dump
save_config

cat ./data/vendor/wifi/wpa/wpa_supplicant.conf

cat << EOF > /data/vendor/wifi/wpa/wpa_supplicant.conf
ctrl_interface=/data/vendor/wifi/wpa/sockets
disable_scan_offload=1
update_config=1
p2p_add_cli_chan=1
filter_rssi=-75
pmf=1
sae_pwe=2
external_sim=1
wowlan_triggers=any
gas_rand_addr_lifetime=0
gas_rand_mac_addr=1
p2p_disabled=0

network={
        ssid="MagentaWLAN-L6J9"
        psk="07764007550070636230"
        key_mgmt=WPA-PSK FT-PSK WPA-PSK-SHA256
        group=CCMP TKIP
        id_str="%7B%22configKey%22%3A%22%5C%22MagentaWLAN-L6J9%5C%22WPA_PSK%22%2C%22creatorUid%22%3A%221000%22%7D"
}
EOF

set p2p_disabled 0
help p2p_find

logcat wpa_supplicant:V *:S

08-07 18:53:56.966  4420  4420 D wpa_supplicant: wlan0: P2P: Reject p2p_find operation (P2P disabled)

/data/vendor/wifi/wpa/wpa_supplicant.conf

-07 18:47:32.935  4420  4420 D wpa_supplicant: nl80211: Use separate P2P group interface (driver advertised support)
08-07 18:47:32.935  4420  4420 D wpa_supplicant: nl80211: Enable multi-channel concurrent (driver advertised support)
08-07 18:47:32.935  4420  4420 D wpa_supplicant: nl80211: use P2P_DEVICE support

08-07 18:47:32.944  4420  4420 I wpa_supplicant: rfkill: Cannot open RFKILL control device
08-07 18:47:32.944  4420  4420 D wpa_supplicant: nl80211: RFKILL status not available

08-07 18:51:28.153  4420  4420 D wpa_supplicant: wlan0: Control interface command 'P2P_FIND'
08-07 18:51:28.153  4420  4420 D wpa_supplicant: wlan0: P2P: Use 500 ms search delay due to concurrent operation
08-07 18:51:28.153  4420  4420 D wpa_supplicant: wlan0: P2P: Reject p2p_find operation (P2P disabled)

grep -rI --color "global->p2p ="
wpas_p2p_init()

separate p2p interface?

iw list
iw phy phy0 info
iw phy pyh0 interface add p2p0 type p2p-device
iw wdev <idx> p2p start
 */
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
                    SupportsWifiAware()
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