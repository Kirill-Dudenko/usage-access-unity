package com.usage.access.helper;


import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class UsageAccessHelper {
    private static final String TAG = "usage.access.helper";
    private final Context activity;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public UsageAccessHelper(Context activity) {
        this.activity = activity;
    }

    private void makeToast(String m) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show();
    }

    private void log(String m) {
        Log.v(TAG, m);
    }

    public boolean getAccessGranted() {
        boolean result = false;
        try {
            PackageManager packageManager = activity.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(activity.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            result = mode == 0;
        } catch (PackageManager.NameNotFoundException ignored) { }
        return result;
    }

    public final boolean permitUsageAccess(boolean forceToShow) {
        boolean access = getAccessGranted();
        if (access && !forceToShow)
            return true;
        handler.post(() -> {
            waitForUsageAccessPermissionGranted();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
            }
            log("pre-start settings activity");
            activity.startActivity(intent);
            makeToast("Settings activity started!");
        });

        return access;
    }

    private void waitForUsageAccessPermissionGranted() {
        AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        AppOpsManager.OnOpChangedListener watcher = new AppOpsManager.OnOpChangedListener() {
            @Override
            public void onOpChanged(String s, String s1) {
                if (!getAccessGranted())
                    return;
                handler.post(() -> {
                    log("Try to launch activity!");
                    appOpsManager.stopWatchingMode(this);
                    Intent intent = new Intent(activity, activity.getClass());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    log("Unity Activity launched success!");
                });
            }
        };
        appOpsManager.startWatchingMode(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                activity.getPackageName(),
                watcher
        );
    }

}
