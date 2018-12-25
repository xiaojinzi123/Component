package com.ehi.component.bean;

import android.content.Intent;

import com.ehi.component.impl.EHiRouterRequest;

public interface CustomerIntentCall {

    /**
     * 获取创建的 Intent
     *
     * @param request
     * @return
     */
    Intent get(EHiRouterRequest request);

}