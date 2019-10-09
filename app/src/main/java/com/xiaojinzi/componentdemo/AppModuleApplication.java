package com.xiaojinzi.componentdemo;

import android.app.Application;
import androidx.annotation.NonNull;
import android.util.Log;

import com.xiaojinzi.component.anno.ModuleAppAnno;
import com.xiaojinzi.component.application.IComponentApplication;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterListener;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.support.Utils;

@ModuleAppAnno()
public class AppModuleApplication implements IComponentApplication {

    private final String tag = "自定义实现的路由监听";

    private RouterListener listener = new RouterListener() {
        @Override
        public void onSuccess(@NonNull RouterResult successResult) throws Exception {
            RouterRequest originalRequest = successResult.getOriginalRequest();
            Log.e(tag, "路由成功：" + originalRequest.uri.toString() + ",requestCode is " + (originalRequest.requestCode == null ? "null" : originalRequest.requestCode));
        }

        @Override
        public void onError(RouterErrorResult errorResult) throws Exception {
            RouterRequest originalRequest = errorResult.getOriginalRequest();
            if (originalRequest == null) {
                Log.e(tag, "路由失败：没开始就失败了,errorMsg = " + Utils.getRealMessage(errorResult.getError()));
            } else {
                Log.e(tag, "路由失败：" + originalRequest.uri.toString()
                        + ",errorMsg = " + Utils.getRealMessage(errorResult.getError())
                        + "\nrequestCode is " + (originalRequest.requestCode == null ? "null" : originalRequest.requestCode)
                );
            }
        }

        @Override
        public void onCancel(@NonNull RouterRequest originalRequest) throws Exception {
            Log.e(tag, "路由被取消：" + originalRequest.uri.toString() + ",requestCode is " + (originalRequest.requestCode == null ? "null" : originalRequest.requestCode));
        }
    };

    @Override
    public void onCreate(@NonNull Application app) {
        Router.addRouterListener(listener);
    }

    @Override
    public void onDestroy() {
        Router.removeRouterListener(listener);
    }

}
