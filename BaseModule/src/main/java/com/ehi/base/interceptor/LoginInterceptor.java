package com.ehi.base.interceptor;

import android.content.Context;
import android.content.Intent;

import com.ehi.base.bean.User;
import com.ehi.base.service.EHiServiceContainer;
import com.ehi.base.service.inter.user.UserService;
import com.ehi.component.impl.EHiRouterExecuteResult;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.support.EHiRouterInterceptor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class LoginInterceptor implements EHiRouterInterceptor {

    @Override
    public EHiRouterExecuteResult intercept(Chain chain) throws Exception {

        UserService userService = EHiServiceContainer.get(UserService.class);
        if (userService != null && userService.isLogin()) {
            return chain.proceed(chain.request());
        }

        Context context = chain.request().context == null ? chain.request().fragment.getContext() : chain.request().context;

        Intent intent = EHiRxRouter.with(context)
                .host("user")
                .path("login")
                .requestCode(444)
                .newIntentCall()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .blockingGet();

        User user = (User) intent.getSerializableExtra("data");
        if (user != null) {
            return chain.proceed(chain.request());
        }else {
            throw new Exception("login fail");
        }

    }

}
