package com.xiaojinzi.component.support;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 这是一个 {@link com.xiaojinzi.component.impl.RouterRequest} 对象的帮助类
 */
public class RouterRequestHelp {

    @MainThread
    public static void executeBeforAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.beforAction != null) {
            request.beforAction.run();
        }
    }

    @MainThread
    public static void executeAfterAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterAction != null) {
            request.afterAction.run();
        }
        executeAfterEventAction(request);
    }

    @MainThread
    public static void executeAfterErrorAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterErrorAction != null) {
            request.afterErrorAction.run();
        }
        executeAfterEventAction(request);
    }

    @MainThread
    public static void executeAfterEventAction(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterEventAction != null) {
            request.afterEventAction.run();
        }
    }

}
