package com.xiaojinzi.user.view;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterDegradeAnno;
import com.xiaojinzi.component.impl.RouterDegrade;
import com.xiaojinzi.component.impl.RouterRequest;

@RouterDegradeAnno(priority = 1)
public class UserModuleRouterDegrade implements RouterDegrade {

    @Override
    public boolean isMatch(@NonNull RouterRequest request) {
        return ModuleConfig.User.NAME.equals(request.uri.getHost());
    }

    @NonNull
    @Override
    public Intent onDegrade(@NonNull RouterRequest request) {
        return new Intent(request.getRawContext(), UserDegradeAct.class);
    }

}
