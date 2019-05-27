package com.xiaojinzi.component.support;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import android.util.Log;

import com.xiaojinzi.component.ComponentConfig;

/**
 * 用于打印日志
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public class LogUtil {

    private LogUtil() {
    }

    @AnyThread
    public static void log(@NonNull String tag, @NonNull String message) {
        if (ComponentConfig.isDebug()) {
            Log.d(tag, message);
        }
    }

    @AnyThread
    public static void log(@NonNull String message) {
        log("Component", message);
    }

}
