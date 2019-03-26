package com.ehi.componentdemo;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.Router;
import com.ehi.component.impl.EHiRouterErrorResult;
import com.ehi.component.impl.EHiRouterListener;
import com.ehi.component.impl.RouterRequest;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.support.Utils;

@EHiModuleAppAnno()
public class AppModuleApplication implements IComponentApplication {

    private final String tag = "appModule";

    private EHiRouterListener listener = new EHiRouterListener() {
        @Override
        public void onSuccess(@NonNull EHiRouterResult successResult) throws Exception {
            RouterRequest originalRequest = successResult.getOriginalRequest();
            Log.e(tag, "路由成功：" + originalRequest.uri.toString() + ",requestCode is " + (originalRequest.requestCode == null ? "null" : originalRequest.requestCode));
        }

        @Override
        public void onError(EHiRouterErrorResult errorResult) throws Exception {
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
    public void onDestory() {
        Router.removeRouterListener(listener);
    }

}
