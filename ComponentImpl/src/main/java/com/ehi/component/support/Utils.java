package com.ehi.component.support;

import android.support.annotation.NonNull;

/**
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public class Utils {

    /**
     * 获取真实错误的信息
     *
     * @param throwable
     * @return
     */
    public static String getRealMessage(@NonNull Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable.getMessage();
    }

}
