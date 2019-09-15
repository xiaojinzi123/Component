package com.xiaojinzi.componentdemo;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.RouterDegradeAnno;
import com.xiaojinzi.component.impl.RouterDegrade;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.componentdemo.view.InfoAct;

/**
 * 优先级很低, 全局的一个处理
 */
@RouterDegradeAnno(priority = -1)
public class AppErrorRouterDegrade implements RouterDegrade {

    @Override
    public boolean isMatch(@NonNull RouterRequest request) {
        return false;
    }

    @NonNull
    @Override
    public Intent onDegrade(@NonNull RouterRequest request) {
        return new Intent(request.getRawContext(), InfoAct.class);
    }

}
