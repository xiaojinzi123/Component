package com.xiaojinzi.base.router;

import android.app.Activity;
import android.content.Context;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

@RouterApiAnno()
@HostAnno(ModuleConfig.App.NAME)
public interface AppApi {

    @PathAnno(ModuleConfig.App.TEST_ROUTER)
    void goToTestRouter(Activity context);

    @PathAnno(ModuleConfig.App.TEST_QUALITY)
    void goToTestQuality(Context context);

    @HostAnno((ModuleConfig.Help.NAME))
    @PathAnno((ModuleConfig.Help.TEST_WEB_ROUTER))
    void goToTestWebRouter(Context context);

}
