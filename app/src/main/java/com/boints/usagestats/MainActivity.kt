package com.boints.usagestats

import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.lifecycle.lifecycleScope
import com.boints.usagestats.ui.theme.UsageStatsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    var accessGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Activity recreated!", Toast.LENGTH_LONG).show()
        waitForUsageAccessPermissionGranted()
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
                                permitUsageAccess()
                            }
                        ) {
                            Text("Open settings")
                        }
                    }
                }
            }
        }
    }

    fun permitUsageAccess(specifyPackage: Boolean = true) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

        if (specifyPackage)
            intent.data = Uri.parse("package:$packageName")

//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Чтобы не оставалось в "последних" при возврате в приложение
        startActivity(intent)
    }

    fun waitForUsageAccessPermissionGranted(): Boolean {
        val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager

        accessGranted = isAccessGranted()
        if(accessGranted)
            return true

        fun grantPermissionWatcher(op: String, packageName: String) {
            accessGranted = isAccessGranted()
            if(!accessGranted)
                return

            lifecycleScope.launch(Dispatchers.Main) {
                Log.v("test456", "Try to launch activity!")
                //appOpsManager.stopWatchingMode(::watcher)
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                Toast.makeText(this@MainActivity, "Start: $intent", Toast.LENGTH_LONG).show()
                Log.v("test456", "Activity launched success!")
            }
        }

        appOpsManager.startWatchingMode(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationContext.packageName,
            ::grantPermissionWatcher
        )
        return false
    }

    fun isAccessGranted(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid, applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}