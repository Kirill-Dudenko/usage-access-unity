package com.boints.usagestats

import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.boints.usagestats.ui.theme.UsageStatsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    var accessGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            while(!accessGranted){
                accessGranted = isAccessGranted()
                delay(500)
            }
        }
        setContent {
            UsageStatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column{
                        Text("Access granted: $accessGranted")
                        Button(
                            onClick = {
                                showDialog()
                            }
                        ){
                            Text("Open settings")
                        }
                    }

                }
            }
        }
    }

    fun showDialog() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            .putExtra("android.provider.extra.APP_PACKAGE", packageName)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(intent, 0)
    }

    private fun isAccessGranted(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid, applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}