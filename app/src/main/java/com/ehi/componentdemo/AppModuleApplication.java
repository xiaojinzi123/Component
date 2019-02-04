package com.ehi.componentdemo;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterErrorResult;
import com.ehi.component.impl.EHiRouterListener;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.support.Utils;

@EHiModuleAppAnno()
public class AppModuleApplication implements IComponentApplication {

    private final String tag = "appModule";

    private EHiRouterListener listener = new EHiRouterListener() {
        @Override
        public void onSuccess(@NonNull EHiRouterResult successResult) throws Exception {
            Log.e(tag, "路由成功：" + successResult.getOriginalRequest().uri.toString());
        }

        @Override
        public void onError(EHiRouterErrorResult errorResult) throws Exception {
            if (errorResult.getOriginalRequest() == null) {
                Log.e(tag, "路由失败：没开始就失败了,errorMsg = " + Utils.getRealMessage(errorResult.getError()));
            } else {
                Log.e(tag, "路由失败：" + errorResult.getOriginalRequest().uri.toString() + ",errorMsg = " + Utils.getRealMessage(errorResult.getError()));
            }
        }

        @Override
        public void onCancel(@NonNull EHiRouterRequest request) throws Exception {
            Log.e(tag, "路由被取消：" + request.uri.toString());
        }
    };

    @Override
    public void onCreate(@NonNull Application app) {
        EHiRouter.addRouterListener(listener);
    }

    @Override
    public void onDestory() {
        EHiRouter.removeRouterListener(listener);
    }

}
