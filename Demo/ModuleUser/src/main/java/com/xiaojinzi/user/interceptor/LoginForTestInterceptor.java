package com.xiaojinzi.user.interceptor;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.service.inter.user.UserService;
import com.xiaojinzi.component.anno.InterceptorAnno;
import com.xiaojinzi.component.error.ServiceNotFoundException;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.impl.service.ServiceManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi
 */
@InterceptorAnno(InterceptorConfig.USER_LOGIN_FOR_TEST)
public class LoginForTestInterceptor implements RouterInterceptor {

    public LoginForTestInterceptor(Context app) {
    }

    @Override
    public void intercept(final Chain chain) throws Exception {
        final Context context = chain.request().getRawOrTopActivity();
        UserService userService = ServiceManager.get(UserService.class, "user1");
        if (chain.request().uri.toString().contains("user/login")) {
            chain.proceed(chain.request());
            return;
        }
        if (userService == null) {
            chain.callback().onError(new ServiceNotFoundException("can't found UserService"));
            return;
        } else if (userService.isLogin()) {
            Toast.makeText(context, "已经登录,正在帮您跳转", Toast.LENGTH_SHORT).show();
            chain.proceed(chain.request());
            return;
        } else {
            Toast.makeText(context, "目标界面需要登录,拦截器帮您跳转到登录界面登录", Toast.LENGTH_SHORT).show();
            RxRouter.with(context)
                    .host(ModuleConfig.User.NAME)
                    .path(ModuleConfig.User.LOGIN)
                    .requestCodeRandom()
                    .putBoolean("isReturnAuto", true)
                    .intentCall()
                    .doOnSuccess(new Consumer<Intent>() {
                        @Override
                        public void accept(Intent intent) throws Exception {
                            Toast.makeText(context, "登录成功,1秒后跳转到目标界面", Toast.LENGTH_SHORT).show();
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
                            chain.callback().onError(new Exception("login fail"));
                        }
                    });
        }
    }

}
