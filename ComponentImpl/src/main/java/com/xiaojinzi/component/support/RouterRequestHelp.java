package com.xiaojinzi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 这是一个 {@link com.xiaojinzi.component.impl.RouterRequest} 对象的帮助类
 */
public class RouterRequestHelp {

    @MainThread
    public static void executeBeforCallback(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.beforJumpAction != null) {
            request.beforJumpAction.run();
        }
    }

    @MainThread
    public static void executeAfterJumpCallback(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterJumpAction != null) {
            request.afterJumpAction.run();
        }
        executeAfterEventCallback(request);
    }

    @MainThread
    public static void executeAfterErrorCallback(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterErrorAction != null) {
            request.afterErrorAction.run();
        }
        executeAfterEventCallback(request);
    }

    @MainThread
    public static void executeAfterEventCallback(@NonNull RouterRequest request) throws Exception {
        Utils.checkNullPointer(request, "request");
        if (request.afterEventAction != null) {
            request.afterEventAction.run();
        }
    }

}
