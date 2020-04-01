package com.xiaojinzi.component.impl;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.support.Consumer1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 为了完成跳转拿 {@link ActivityResult} 的功能, 需要预埋一个 {@link Fragment}
 * 此 {@link Fragment} 是不可见的, 它的主要作用：
 * 1. 用作跳转的
 * 2. 用作接受 {@link Fragment#onActivityResult(int, int, Intent)} 的回调的值
 *
 * 当发出多个路由的之后, 有可能多个 {@link RouterRequest} 对象中的 {@link RouterRequest#requestCode}
 * 是一致的, 那么就会造成 {@link ActivityResult} 回调的时候出现不对应的问题.
 * 这个问题在 {@link com.xiaojinzi.component.impl.Navigator.Help#isExist(RouterRequest)} 方法中
 * 得以保证
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 */
public final class RouterFragment extends Fragment {

    public RouterFragment() {
    }

    @NonNull
    private Map<RouterRequest, Consumer1<ActivityResult>> singleEmitterMap = new HashMap<>();

    @Override
    public void onDestroy() {
        if (singleEmitterMap != null) {
            singleEmitterMap.clear();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 根据 requestCode 获取发射器
        Consumer1<ActivityResult> findConsumer = null;
        RouterRequest findRequest = null;
        // 找出 requestCode 一样的那个
        Set<RouterRequest> keySet = singleEmitterMap.keySet();
        for (RouterRequest request : keySet) {
            if (request.requestCode != null && request.requestCode.equals(requestCode)) {
                findRequest = request;
                break;
            }
        }
        if (findRequest != null) {
            findConsumer = singleEmitterMap.get(findRequest);
        }
        if (findConsumer != null) {
            try {
                findConsumer.accept(new ActivityResult(requestCode, resultCode, data));
            } catch (Exception ignore) {
                // ignore
            }
        }
        if (findRequest != null) {
            singleEmitterMap.remove(findRequest);
        }

    }

    public void setActivityResultConsumer(@NonNull RouterRequest request,
                                          @NonNull Consumer1<ActivityResult> consumer) {
        // 检测是否重复的在这个方法调用之前被检查掉了
        singleEmitterMap.put(request, consumer);
    }

    public void removeActivityResultConsumer(@NonNull RouterRequest request) {
        singleEmitterMap.remove(request);
    }

}
