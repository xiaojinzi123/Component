package com.xiaojinzi.base.router;

import android.content.Context;
import android.content.Intent;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.NavigateAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RequestCodeAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.impl.BiCallback;

@RouterApiAnno()
@HostAnno(ModuleConfig.Module1.NAME)
public interface Module1Api {

    @PathAnno(ModuleConfig.Module1.TEST)
    void toTestView(Context context,
                    @ParameterAnno("data") String data);

    @NavigateAnno(forIntent = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    @RequestCodeAnno()
    void toTestView(Context context, @ParameterAnno("data") String data,
                    BiCallback<Intent> callback);

}
