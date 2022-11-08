package com.boints.usagestats

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.boints.usagestats.ui.theme.UsageStatsTheme
import android.content.Intent

import android.view.WindowManager

import android.content.DialogInterface

import android.os.Build

import android.annotation.TargetApi

import android.app.AlertDialog

import android.app.usage.UsageStatsManager

import android.app.usage.UsageStats
import android.content.Context
import android.provider.Settings
import android.util.Log


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UsageStatsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
        showDialog()
    }

    fun showDialog() {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val appList = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 1000, time
        )
        if (appList.size == 0) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Usage Access")
                .setMessage("App will not run without usage access permissions.")
                .setPositiveButton(
                    "Settings"
                ) { _, _ -> // continue with delete
//                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, which -> // do nothing
                    dialog.dismiss()
                }
                .setIcon(R.drawable.ic_dialog_alert)
                .create()
            alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            alertDialog.show()
        } else {
            /* val intent = Intent(this, PackageService::class.java)
             startService(intent)
             finish()*/
            Log.v("TEST", "Usage permission granted")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UsageStatsTheme {
        Greeting("Android")
    }
}