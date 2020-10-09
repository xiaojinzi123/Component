package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 这是一个 {@link com.xiaojinzi.component.impl.RouterRequest} 对象的帮助类
 */
public class RouterRequestHelp {

    @UiThread
    public static void executeBeforAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.beforAction != null) {
            request.beforAction.run();
        }
    }

    @UiThread
    public static void executeAfterAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterAction != null) {
            request.afterAction.run();
        }
        executeAfterEventAction(request);
    }

    @UiThread
    public static void executeAfterErrorAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterErrorAction != null) {
            request.afterErrorAction.run();
        }
        executeAfterEventAction(request);
    }

    @UiThread
    public static void executeAfterEventAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterEventAction != null) {
            request.afterEventAction.run();
        }
    }

}
