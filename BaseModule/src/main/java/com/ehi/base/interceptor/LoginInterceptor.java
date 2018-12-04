package com.ehi.base.interceptor;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ehi.base.ModuleConfig;
import com.ehi.base.bean.User;
import com.ehi.base.service.EHiServiceContainer;
import com.ehi.base.service.inter.user.UserService;
import com.ehi.component.impl.EHiRxRouter;
import com.ehi.component.support.EHiRouterInterceptor;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 其实你可以做的事情很多
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class LoginInterceptor implements EHiRouterInterceptor {

    @Override
    public void intercept(final Chain chain) throws Exception {

        final Context context = chain.request().context == null ? chain.request().fragment.getContext() : chain.request().context;
        UserService userService = EHiServiceContainer.get(UserService.class);

        if (chain.request().uri.toString().contains("user/login")) {
            chain.proceed(chain.request());
            return;
        }

        if ((userService != null && userService.isLogin())) {
            Toast.makeText(context, "已经登录,正在帮您跳转,", Toast.LENGTH_SHORT).show();
            chain.proceed(chain.request());
            return;
        }

        Toast.makeText(context, "目标界面需要登录,拦截器帮您跳转到登录界面登录,", Toast.LENGTH_SHORT).show();

        EHiRxRouter.with(context)
                .host(ModuleConfig.User.NAME)
                .path(ModuleConfig.User.LOGIN)
                .requestCode(444)
                .newIntentCall()
                .doOnSuccess(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        Toast.makeText(context, "登录成功,1秒后跳转到目标界面,", Toast.LENGTH_SHORT).show();
                    }
                })
                .observeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        User user = (User) intent.getSerializableExtra("data");
                        if (user != null) {
                            chain.proceed(chain.request());
                        } else {
                            chain.callback().onError(new Exception("login fail"));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        chain.callback().onError(new Exception(throwable));
                    }
                });


    }

}
