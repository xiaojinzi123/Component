package com.xiaojinzi.component2

import android.content.Context
import com.xiaojinzi.base.ModuleConfig
import com.xiaojinzi.component.anno.ParameterAnno
import com.xiaojinzi.component.anno.router.*
import com.xiaojinzi.component.support.Action
import io.reactivex.Completable

@RouterApiAnno
@HostAnno(ModuleConfig.App.NAME)
interface AppApiKotlin {

    @UserInfoAnno("xiaojinzi")
    @PathAnno(ModuleConfig.App.TEST_ROUTER)
    fun goToTestRouter(@AfterActionAnno afterAction: Action)

    @PathAnno(ModuleConfig.App.TEST_QUALITY)
    fun goToTestQuality(): Completable

    @HostAnno(ModuleConfig.Help.NAME)
    @PathAnno(ModuleConfig.Help.TEST_WEB_ROUTER)
    fun goToTestWebRouter(context: Context)

    @PathAnno(ModuleConfig.Help.TEST_WEB_ROUTER)
    fun test(
            context: Context,
            @ParameterAnno("name") name: String,
            @ParameterAnno("age") age: Int? = 0
    )

}