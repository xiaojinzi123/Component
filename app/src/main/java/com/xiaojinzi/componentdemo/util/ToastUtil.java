package com.xiaojinzi.componentdemo.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.xiaojinzi.component.Component;

/**
 * time   : 2019/01/25
 *
 * @author : xiaojinzi
 */
public class ToastUtil {

    private static Handler h = new Handler(Looper.getMainLooper());

    public static void toastShort(final String message) {
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Component.getApplication(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toastLong(final String message) {
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Component.getApplication(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
