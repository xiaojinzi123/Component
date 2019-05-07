package com.xiaojinzi.component;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.impl.Callback;
import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * 当你构建完毕一切,但是不想立马发生一次路由的时候可以生成一个 {@link Call}
 */
public interface Call {

    /**
     * 执行之后可以拿到一个 {@link NavigationDisposable} 对象,可用于取消
     *
     * @param callBack
     * @return NavigationDisposable 可能是一个 {@link com.xiaojinzi.component.impl.Router#emptyNavigationDisposable}
     */
    @NonNull
    NavigationDisposable execute(@NonNull Callback callBack);

}
