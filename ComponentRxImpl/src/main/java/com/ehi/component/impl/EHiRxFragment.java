package com.ehi.component.impl;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.ehi.component.bean.EHiActivityResult;
import com.ehi.component.support.Consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 跳转界面拿数据结合 RxJava2 的 Fragment
 * 同一个 EHiRxFragment 内承载的路由请求的 requestCode 不能同时相同,单个重复是可以的,同一个
 * requestCode 被连续使用两次,这两次的路由都在进行中,这种情况是被明确禁止的
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 * @hide
 */
public final class EHiRxFragment extends Fragment {

    @NonNull
    private Map<EHiRouterRequest, Consumer<EHiActivityResult>> singleEmitterMap = new HashMap<>();

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
        Consumer<EHiActivityResult> findConsumer = null;
        EHiRouterRequest findRequest = null;
        // 找出 requestCode 一样的那个
        Set<EHiRouterRequest> keySet = singleEmitterMap.keySet();
        for (EHiRouterRequest request : keySet) {
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
                findConsumer.accept(new EHiActivityResult(requestCode, resultCode, data));
            } catch (Exception ignore) {
                // ignore
            }
        }
        if (findRequest != null) {
            singleEmitterMap.remove(findRequest);
        }

    }

    public boolean isContainsSingleEmitter(@NonNull EHiRouterRequest request) {
        return singleEmitterMap.containsKey(request);
    }

    public void setSingleEmitter(@NonNull EHiRouterRequest request, @NonNull Consumer<EHiActivityResult> consumer) {
        // 检测是否重复的在这个方法调用之前被检查掉了
        singleEmitterMap.put(request, consumer);
    }

    public void cancal(@NonNull int requestCode) {
        singleEmitterMap.remove(requestCode);
    }

}
