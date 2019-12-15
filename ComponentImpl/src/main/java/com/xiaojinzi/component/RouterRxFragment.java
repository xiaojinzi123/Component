package com.xiaojinzi.component;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.support.Consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 跳转界面拿数据结合 RxJava2 的 Fragment
 * 同一个 RouterRxFragment 内承载的路由请求的 requestCode 不能同时相同,单个重复是可以的,同一个
 * requestCode 被连续使用两次,这两次的路由都在进行中,这种情况是被明确禁止的
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 * @hide
 */
public final class RouterRxFragment extends Fragment {

    @NonNull
    private Map<RouterRequest, Consumer<ActivityResult>> singleEmitterMap = new HashMap<>();

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
        Consumer<ActivityResult> findConsumer = null;
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
                                          @NonNull Consumer<ActivityResult> consumer) {
        // 检测是否重复的在这个方法调用之前被检查掉了
        singleEmitterMap.put(request, consumer);
    }

    public void removeActivityResultConsumer(@NonNull RouterRequest request) {
        singleEmitterMap.remove(request);
    }

}
