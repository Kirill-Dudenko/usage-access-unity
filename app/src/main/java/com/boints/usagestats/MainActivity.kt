package com.boints.usagestats

import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.boints.usagestats.ui.theme.UsageStatsTheme


class MainActivity : ComponentActivity() {
    var accessGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        watchForUsageStatsPermission()
        setContent {
            UsageStatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Text("Access granted: $accessGranted")
                        Button(
                            onClick = {
                                showUsageStatsPermissionDialog()
                            }
                        ) {
                            Text("Open settings")
                        }
                    }
                }
            }
        }
    }

    fun showUsageStatsPermissionDialog(specifyPackage: Boolean = true) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

        if (specifyPackage)
            intent.data = Uri.parse("package:$packageName")

        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent,)
    }

    private fun watchForUsageStatsPermission(): Unit {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )

        if (mode == AppOpsManager.MODE_ALLOWED) {
            accessGranted = true
            return
        }

        fun watcher(op: String, packageName: String) {
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), getPackageName()
            )
            if (mode != AppOpsManager.MODE_ALLOWED) {
                accessGranted = false
                return
            }
            appOps.stopWatchingMode(::watcher)
            accessGranted = true
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            applicationContext.startActivity(intent)
        }

        appOps.startWatchingMode(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationContext.packageName,
            ::watcher
        )
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