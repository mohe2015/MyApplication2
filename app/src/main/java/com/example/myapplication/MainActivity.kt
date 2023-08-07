package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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