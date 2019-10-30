package com.xiaojinzi.component.impl.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.support.Function1;
import com.xiaojinzi.component.support.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment 的容器
 *
 * @author xiaojinzi 30212
 */
@CheckClassName
public class FragmentManager {

    private FragmentManager() {
    }

    /**
     * Service 的集合
     */
    private static Map<String, Function1<Bundle, ? extends Fragment>> map = new HashMap<>();

    /**
     * 你可以注册一个服务,服务的初始化可以是 懒加载的
     *
     * @param flag
     * @param function
     */
    public static void register(@NonNull String flag, @NonNull Function1<Bundle, ? extends Fragment> function) {
        Utils.checkNullPointer(flag, "flag");
        Utils.checkNullPointer(function, "function");
        map.put(flag, function);
    }

    @Nullable
    public static void unregister(@NonNull String flag) {
        map.remove(flag);
    }

    @Nullable
    public static Fragment get(@NonNull String flag, @Nullable Bundle bundle) {
        Function1<Bundle, ? extends Fragment> function = map.get(flag);
        if (function == null) {
            return null;
        } else {
            return function.apply(bundle);
        }
    }

}
