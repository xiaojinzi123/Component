package com.ehi.componentdemo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterRequest;

public class CustomerRouterImpl {

    @Nullable
    @EHiRouterAnno(host = ModuleConfig.System.NAME, value = ModuleConfig.System.CALL_PHONE)
    public static Intent callPhoneIntent(@NonNull EHiRouterRequest request) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "15857913627"));
        return intent;
    }

}
