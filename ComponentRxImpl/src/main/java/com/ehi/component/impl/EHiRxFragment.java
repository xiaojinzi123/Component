package com.ehi.component.impl;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleEmitter;

/**
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public final class EHiRxFragment extends Fragment {

    @NonNull
    private Map<Integer, SingleEmitter<Intent>> singleEmitterMap = new HashMap<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SingleEmitter<Intent> singleEmitter = singleEmitterMap.get(requestCode);

        if (singleEmitter != null) {
            if (!singleEmitter.isDisposed()) {
                if (data == null) {
                    singleEmitter.onError(new IntentResultException());
                }else {
                    singleEmitter.onSuccess(data);
                }
            }
        }
        singleEmitterMap.remove(requestCode);
    }


    public boolean isContainsSingleEmitter(@NonNull int key) {
        return singleEmitterMap.containsKey(key);
    }

    public void setSingleEmitter(int requestCode, @NonNull SingleEmitter<Intent> singleEmitter) {
        if (isContainsSingleEmitter(requestCode)) {
            throw new RuntimeException("request code: " + requestCode + " can't be same");
        }
        singleEmitterMap.put(requestCode, singleEmitter);
    }

}
