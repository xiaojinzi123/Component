package com.xiaojinzi.component.support;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 这是一个 {@link com.xiaojinzi.component.impl.RouterRequest} 对象的帮助类
 */
public class RouterRequestHelp {

    @UiThread
    public static void executeBeforeAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.beforeAction != null) {
            request.beforeAction.invoke();
        }
    }

    @UiThread
    public static void executeAfterAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterAction != null) {
            request.afterAction.invoke();
        }
        executeAfterEventAction(request);
    }

    @UiThread
    public static void executeAfterErrorAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterErrorAction != null) {
            request.afterErrorAction.invoke();
        }
        executeAfterEventAction(request);
    }

    @UiThread
    public static void executeAfterEventAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterEventAction != null) {
            request.afterEventAction.invoke();
        }
    }

}
