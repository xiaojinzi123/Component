package com.ehi.component.cache;


import android.app.ActivityManager;
import android.os.Build;

/**
 * <pre>
 * @author : zbb 33775
 * @Date: 2019/2/20 16:22
 * </pre>
 */
public class CacheHelper {

    public static boolean isLowMemoryDevice(ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return activityManager.isLowRamDevice();
        } else {
            return true;
        }
    }
}
