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
import usage.access.permission.helper.UsageAccessHelper


class MainActivity : ComponentActivity() {
    var accessGranted by mutableStateOf(false)
    val helper = UsageAccessHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessGranted = helper.accessGranted
        Toast.makeText(this, "Activity recreated!", Toast.LENGTH_LONG).show()
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
                                accessGranted = helper.permitUsageAccess(forceToShow = true)
                            }
                        ) {
                            Text("Open settings")
                        }
                    }
                }
            }
        }
    }
}