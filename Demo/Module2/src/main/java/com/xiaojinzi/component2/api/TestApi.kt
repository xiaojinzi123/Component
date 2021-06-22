package com.xiaojinzi.component2.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.base.interceptor.TimeConsumingInterceptor
import com.xiaojinzi.component.anno.ParameterAnno
import com.xiaojinzi.component.anno.router.*
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.impl.Call
import io.reactivex.Completable

@RouterApiAnno
interface TestApi {

    @HostAndPathAnno("user/detail")
    fun go(
            context: Context,
            @ParameterAnno("userId") userId: String
    ): Completable

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

    @NavigateAnno(resultCodeMatch = Activity.RESULT_CANCELED)
    @HostAndPathAnno("test/test")
    @UseInteceptorAnno(classes = [TimeConsumingInterceptor::class])
    suspend fun go7()

}