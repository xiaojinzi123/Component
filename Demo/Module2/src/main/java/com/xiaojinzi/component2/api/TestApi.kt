package com.xiaojinzi.component2.api

import android.app.Activity
import android.content.Intent
import com.xiaojinzi.component.anno.router.HostAndPathAnno
import com.xiaojinzi.component.anno.router.NavigateAnno
import com.xiaojinzi.component.anno.router.RouterApiAnno
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.impl.Call

@RouterApiAnno
interface TestApi {

    @HostAndPathAnno("test/test")
    suspend fun go()

    @NavigateAnno(forResult = true)
    @HostAndPathAnno("test/test")
    suspend fun go1(): ActivityResult

    @NavigateAnno(forIntent = true)
    @HostAndPathAnno("test/test")
    suspend fun go2(): Intent

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @HostAndPathAnno("test/test")
    suspend fun go3(): Intent

    @NavigateAnno(forResultCode = true)
    @HostAndPathAnno("test/test")
    suspend fun go4(): Int

    @NavigateAnno
    @HostAndPathAnno("test/test")
    suspend fun go5()

    @NavigateAnno(resultCodeMatch = Activity.RESULT_CANCELED)
    @HostAndPathAnno("test/test")
    suspend fun go6()

}