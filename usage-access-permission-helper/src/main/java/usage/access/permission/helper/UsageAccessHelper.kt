package usage.access.permission.helper

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UsageAccessHelper(
    /**
     * Unity main activity
     */
    private val activity: Context
) {
    val accessGranted: Boolean
        get() {
            return try {
                val packageManager = activity.packageManager
                val applicationInfo = packageManager.getApplicationInfo(activity.packageName, 0)
                val appOpsManager =
                    activity.getSystemService(ComponentActivity.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
                )
                mode == AppOpsManager.MODE_ALLOWED
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    /**
     * Prompt usage access permission from user.
     * @forceToShow show settings activity always (even if permission is granted)
     * @return has permission already granted?
     */
    @JvmOverloads
    fun permitUsageAccess(forceToShow: Boolean = false): Boolean {
        val access = accessGranted
        if (access && !forceToShow)
            return true

        waitForUsageAccessPermissionGranted()
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            intent.data = Uri.parse("package:${activity.packageName}")
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Чтобы не оставалось в "последних" при возврате в приложение
        activity.startActivity(intent)
        return access
    }

    private fun waitForUsageAccessPermissionGranted(): Unit {
        val appOpsManager =
            activity.getSystemService(ComponentActivity.APP_OPS_SERVICE) as AppOpsManager

        fun grantPermissionWatcher(op: String, packageName: String) {
            if (!accessGranted)
                return

            GlobalScope.launch(Dispatchers.Main) {
                log("Try to launch activity!")
                appOpsManager.stopWatchingMode(::grantPermissionWatcher)
                val intent = Intent(activity, activity.javaClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(intent)
                log("Activity launched success!")
            }
        }

        appOpsManager.startWatchingMode(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            activity.packageName,
            ::grantPermissionWatcher
        )
    }
}