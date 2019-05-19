package com.xiaojinzi.base.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.bean.UserWithParcelable;
import com.xiaojinzi.base.bean.UserWithSerializable;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.Navigate;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.anno.router.WithAnno;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.impl.BiCallback;
import com.xiaojinzi.component.impl.Call;
import com.xiaojinzi.component.impl.Callback;
import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * App 模块的路由跳转接口
 */
@RouterApiAnno()
@HostAnno(ModuleConfig.Module1.NAME)
public interface Module1Api {

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test(@WithAnno Context context,
              @ParameterAnno("data") String data, Callback callback);

    @Navigate
    @HostAnno(ModuleConfig.Module2.NAME)
    @PathAnno(ModuleConfig.Module1.TEST)
    void test1(@WithAnno Fragment fragment,
               @ParameterAnno("data") String data);

    @Navigate(forResult = true)
    @HostAndPathAnno(ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST)
    void test2(@WithAnno Context context,
               @ParameterAnno("data") String data, BiCallback<ActivityResult> callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test3(@WithAnno Context context,
                               @ParameterAnno("data") String data,
                               Callback callback);

    @PathAnno(ModuleConfig.Module1.TEST)
    Call test4(@WithAnno Context context,
               @ParameterAnno("data") String data);

    @Navigate(forResult = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test5(@WithAnno Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<ActivityResult> callback);

    @Navigate(forIntent = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test6(@WithAnno Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Intent> callback);

    @Navigate(forResultCode = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test7(@WithAnno Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Integer> callback);

    @Navigate(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test8(@WithAnno Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Intent> callback);

    @Navigate(resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    void test9(@WithAnno Context context,
               @ParameterAnno("data") String data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test10(@WithAnno Context context,
                @ParameterAnno("data1") byte data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test11(@WithAnno Context context,
                @ParameterAnno("data1") short data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test12(@WithAnno Context context,
                @ParameterAnno("data1") int data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test13(@WithAnno Context context,
                @ParameterAnno("data1") long data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test14(@WithAnno Context context,
                @ParameterAnno("data1") float data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test15(@WithAnno Context context,
                @ParameterAnno("data1") double data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test16(@WithAnno Context context,
                @ParameterAnno("data1") String data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test17(@WithAnno Context context,
                @ParameterAnno("data1") User data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test18(@WithAnno Context context,
                @ParameterAnno("data1") UserWithParcelable data, Callback callback);

    @Navigate
    @PathAnno(ModuleConfig.Module1.TEST)
    void test19(@WithAnno Context context,
                @ParameterAnno("data1") UserWithSerializable data, Callback callback);


}
