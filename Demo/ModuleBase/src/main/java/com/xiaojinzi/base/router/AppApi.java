package com.xiaojinzi.base.router;

import android.content.Context;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.router.AfterActionAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.anno.router.UserInfoAnno;
import com.xiaojinzi.component.support.Action;

import io.reactivex.Completable;

@RouterApiAnno()
@HostAnno(ModuleConfig.App.NAME)
public interface AppApi {

    @UserInfoAnno("xiaojinzi")
    @PathAnno(ModuleConfig.App.TEST_ROUTER)
    void goToTestRouter(@AfterActionAnno Action afterAction);

    @PathAnno(ModuleConfig.App.TEST_QUALITY)
    Completable goToTestQuality();

    @HostAnno(ModuleConfig.Help.NAME)
    @PathAnno((ModuleConfig.Help.TEST_WEB_ROUTER))
    void goToTestWebRouter(Context context);

}
