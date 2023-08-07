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

https://source.android.com/docs/core/connect/wifi-hal

interface_list

https://www.spinics.net/lists/hostap/msg01394.html

./vendor/etc/wifi/wpa_supplicant_overlay.conf # read only

mount | grep vendor
mount -o rw,remount /vendor
sed -i 's/p2p_disabled=1/p2p_disabled=0/g' ./vendor/etc/wifi/wpa_supplicant_overlay.conf
cat ./vendor/etc/wifi/wpa_supplicant_overlay.conf

iw dev wlan0 set power_save off
ip link

https://github.com/raspberry-vanilla

https://github.com/sonyxperiadev/device-sony-loire/blob/q-mr1/rootdir/vendor/etc/wifi/wpa_supplicant_overlay.conf#L3
 https://github.com/raspberry-vanilla/android_device_brcm_rpi4/blob/b11baea4dd8d6fe17a031f5732b2afcbb7e382d8/device.mk#L273

scan_results
interface_list

/dev/socket/wpa_wlan0

   26.865441] ------------[ cut here ]------------
[   26.865465] WARNING: CPU: 1 PID: 604 at drivers/net/wireless/broadcom/brcm80211/brcmfmac/cfg80211.c:5517 brcmf_cfg80211_set_pmk+0x178/0x184
[   26.865489] Modules linked in:
[   26.865498] CPU: 1 PID: 604 Comm: wpa_supplicant Not tainted 5.15.106-v8-g8d0475872f62 #1
[   26.865506] Hardware name: Raspberry Pi 4 Model B Rev 1.4 (DT)
[   26.865511] pstate: 80400005 (Nzcv daif +PAN -UAO -TCO -DIT -SSBS BTYPE=--)
[   26.865519] pc : brcmf_cfg80211_set_pmk+0x178/0x184
[   26.865526] lr : brcmf_cfg80211_set_pmk+0x5c/0x184
[   26.865533] sp : ffffffc00adc3750
[   26.865537] x29: ffffffc00adc37a0 x28: ffffff815c760000 x27: ffffff8174f1ee00
[   26.865547] x26: ffffffe9d6dde5fc x25: ffffff8174f1ee10 x24: ffffffe9d7a70ec0
[   26.865557] x23: ffffff8102a78008 x22: ffffffc00adc3900 x21: ffffffc00adc3838
[   26.865566] x20: 00000000ffffffea x19: ffffff8102574560 x18: ffffffc00aa6d088
[   26.865574] x17: 0000000000000143 x16: ffffffe9d7117f78 x15: ffffff816be8e000
[   26.865583] x14: 0000000000000010 x13: 0000000000000000 x12: 000000000000ff20
[   26.865592] x11: ffffffe9d7a27000 x10: ffffffc00adc3708 x9 : 933d630e9b798900
[   26.865601] x8 : 0000000000000001 x7 : 0000000000000000 x6 : 000000000000003f
[   26.865610] x5 : ffffffffffffffff x4 : 0000000000000000 x3 : ffffffc00adc35d0
[   26.865618] x2 : ffffffe9d744d66d x1 : ffffffe9d73741d7 x0 : 0000000000000002
[   26.865627] Call trace:
[   26.865631] brcmf_cfg80211_set_pmk+0x178/0x184
[   26.865638] rdev_set_pmk+0x44/0x17c
[   26.865647] nl80211_set_pmk+0x120/0x170
[   26.865653] genl_rcv_msg+0x394/0x3c0
[   26.865659] netlink_rcv_skb+0x114/0x12c
[   26.865666] genl_rcv+0x44/0x60
[   26.865671] netlink_unicast_kernel+0xd4/0x1a8
[   26.865678] netlink_unicast+0x108/0x1c0
[   26.865684] netlink_sendmsg+0x2f0/0x3e4
[   26.865690] ____sys_sendmsg+0x188/0x240
[   26.865697] ___sys_sendmsg+0x12c/0x178
[   26.865702] __arm64_sys_sendmsg+0xc8/0x110
[   26.865708] invoke_syscall+0x50/0x114
[   26.865718] el0_svc_common+0xd4/0x120
[   26.865724] do_el0_svc+0x34/0xa4
[   26.865730] el0_svc+0x28/0x64
[   26.865737] el0t_64_sync_handler+0x88/0xec
[   26.865743] el0t_64_sync+0x1a8/0x1ac
[   26.865749] ---[ end trace bfd18133e0d5786e ]---
[   27.074393] brcmfmac: brcmf_cfg80211_set_power_mgmt: power save disabled
[   29.948123] brcmfmac: brcmf_cfg80211_set_power_mgmt: power save enabled
[   30.105875] type=1400 audit(1686814648.035:107): avc: denied { read } fo



hardware/broadcom/wlan/bcmdhd/config/wpa_supplicant_overlay.conf

mount -o rw,remount /vendor
cat << EOF > /vendor/etc/wifi/p2p_supplicant_overlay.conf
disable_scan_offload=1
wowlan_triggers=any
p2p_no_go_freq=5170-5740
p2p_search_delay=0
no_ctrl_interface=
EOF


New P2P Device interface p2p-dev-wlan0 (0x3) created

Failed to set interface 0 to mode 10: -22 (Invalid argument)

scan_results

No service published for: wifip2p

https://github.com/sevenrock/android_device_motorola_msm8226-common/commit/46517ee2c1c0587f21199ff049346ea480273565

https://android.googlesource.com/platform/hardware/broadcom/wlan/+/refs/heads/main/bcmdhd/config

https://github.com/search?q=org%3Araspberry-vanilla%20android.hardware.wifi.direct&type=code

https://github.com/search?q=org%3Araspberry-vanilla+wifi.direct&type=code

interface name wrong?
wifi.direct.interface

WIFI_HAL_INTERFACE_COMBINATIONS

WIFI_HIDL_FEATURE_DUAL_INTERFACE

https://source.android.com/docs/core/connect/wifi-direct

rameworks/native/data/etc/android.hardware.wifi.direct.xml

https://github.com/search?q=org%3Araspberry-vanilla+frameworks%2Fnative%2Fdata%2Fetc%2Fandroid.hardware.&type=code

https://github.com/raspberry-vanilla/android_device_brcm_rpi4/blob/b11baea4dd8d6fe17a031f5732b2afcbb7e382d8/device.mk#L77

https://github.com/raspberry-vanilla/android_device_brcm_rpi4/blob/b11baea4dd8d6fe17a031f5732b2afcbb7e382d8/device.mk#L272

https://android.googlesource.com/platform/hardware/broadcom/wlan/+/refs/heads/main/bcmdhd/config/p2p_supplicant_overlay.conf
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