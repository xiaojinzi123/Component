package com.ehi.component.support;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ehi.component.ComponentConfig;

/**
 * 用于打印日志
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public class LogUtil {

    public static void log(@NonNull String tag, @NonNull String message) {
        if (ComponentConfig.isDebug()) {
            Log.d(tag, message);
        }
    }

    public static void log(@NonNull String message) {
        log("EHiComponent", message);
    }

}
