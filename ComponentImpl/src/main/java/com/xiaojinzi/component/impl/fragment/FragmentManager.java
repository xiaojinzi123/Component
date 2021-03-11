package com.xiaojinzi.component.impl.fragment;

import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.Function1;
import com.xiaojinzi.component.support.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment 的容器
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
public class FragmentManager {

    private FragmentManager() {
    }

    /**
     * Service 的集合, 线程安全
     */
    private static Map<String, Function1<Bundle, ? extends Fragment>> map =
            Collections.synchronizedMap(new HashMap<String, Function1<Bundle, ? extends Fragment>>());

    /**
     * 你可以注册一个服务,服务的初始化可以是 懒加载的
     *
     * @param flag 用 {@link com.xiaojinzi.component.anno.FragmentAnno} 标记 {@link Fragment} 的字符串
     * @param function function
     */
    @AnyThread
    public static void register(@NonNull String flag,
                                @NonNull Function1<Bundle, ? extends Fragment> function) {
        Utils.checkNullPointer(flag, "flag");
        Utils.checkNullPointer(function, "function");
        map.put(flag, function);
    }

    @Nullable
    @AnyThread
    public static void unregister(@NonNull String flag) {
        map.remove(flag);
    }

    @Nullable
    @AnyThread
    public static Fragment get(@NonNull final String flag) {
        return get(flag, null);
    }

    @Nullable
    @AnyThread
    public static Fragment get(@NonNull final String flag, @Nullable final Bundle bundle) {
        Utils.checkNullPointer(flag, "fragment flag");
        Function1<Bundle, ? extends Fragment> function = map.get(flag);
        if (function == null) {
            return null;
        } else {
            return function.apply(bundle);
        }
    }

}
