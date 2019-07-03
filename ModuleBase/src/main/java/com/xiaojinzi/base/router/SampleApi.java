package com.xiaojinzi.base.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.bean.UserWithParcelable;
import com.xiaojinzi.base.bean.UserWithSerializable;
import com.xiaojinzi.base.interceptor.DialogShowInterceptor;
import com.xiaojinzi.base.interceptor.TimeConsumingInterceptor;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.AfterActionAnno;
import com.xiaojinzi.component.anno.router.BeforActionAnno;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.NavigateAnno;
import com.xiaojinzi.component.anno.router.OptionsAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.anno.router.UseInteceptorAnno;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.impl.BiCallback;
import com.xiaojinzi.component.impl.Call;
import com.xiaojinzi.component.impl.Callback;
import com.xiaojinzi.component.impl.Navigator;
import com.xiaojinzi.component.support.Action;
import com.xiaojinzi.component.support.Consumer;
import com.xiaojinzi.component.support.NavigationDisposable;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * App 模块的路由跳转接口
 */
@RouterApiAnno()
@HostAnno(ModuleConfig.Module1.NAME)
public interface SampleApi {

    @PathAnno(ModuleConfig.Module1.TEST)
    // 使用一个拦截器
    @UseInteceptorAnno(
            names = {InterceptorConfig.USER_LOGIN, InterceptorConfig.HELP_CALLPHOEPERMISION},
            classes = {DialogShowInterceptor.class, TimeConsumingInterceptor.class}
    )
    Navigator test(Context context, @ParameterAnno("data") String data, Callback callback,
                   @NonNull @OptionsAnno Bundle options,
                   @NonNull @BeforActionAnno Action beforAction,
                   @NonNull @AfterActionAnno Action afterAction,
                   @NonNull Consumer<Intent> intentConsumer);

    @NavigateAnno
    @HostAnno(ModuleConfig.Module2.NAME)
    @PathAnno(ModuleConfig.Module1.TEST)
    void test1(Fragment fragment,
               @ParameterAnno("data") String data);

    @NavigateAnno(forResult = true)
    @HostAndPathAnno(ModuleConfig.Module1.NAME + "/" + ModuleConfig.Module1.TEST)
    void test2(Context context,
               @ParameterAnno("data") String data, BiCallback<ActivityResult> callback);

    @NavigateAnno
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test3(Context context,
                               @ParameterAnno("data") String data,
                               Callback callback);

    @PathAnno(ModuleConfig.Module1.TEST)
    Call test4(Context context,
               @ParameterAnno("data") String data);

    @NavigateAnno(forResult = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test5(Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<ActivityResult> callback);

    @NavigateAnno(forResult = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    Single<ActivityResult> test5Rx(Context context,
                                   @ParameterAnno("data") String data);

    @NavigateAnno(forIntent = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test6(Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Intent> callback);

    @NavigateAnno(forIntent = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    Single<Intent> test6Rx(Context context,
                           @ParameterAnno("data") String data);

    @NavigateAnno(forResultCode = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test7(Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Integer> callback);

    @NavigateAnno(forResultCode = true)
    @PathAnno(ModuleConfig.Module1.TEST)
    Single<Integer> test7Rx(Context context,
                            @ParameterAnno("data") String data);

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test8(Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Intent> callback);

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    Single<Intent> test8Rx(Context context,
                           @ParameterAnno("data") String data);

    @NavigateAnno(resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    Completable test9(Context context);

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    Single<Intent> test10(Context context);

    @PathAnno(ModuleConfig.Module1.TEST)
    Completable test11(Context context);

    /**
     * 测试基本类型的支持
     */
    @PathAnno(ModuleConfig.Module1.TEST)
    void test111(Context context,
                @ParameterAnno("data1") String data1,
                @ParameterAnno("data2") byte data2,
                @ParameterAnno("data3") short data3,
                @ParameterAnno("data4") int data4,
                @ParameterAnno("data5") long data5,
                @ParameterAnno("data6") float data6,
                @ParameterAnno("data7") double data7,
                @ParameterAnno("data8") User data8,
                @ParameterAnno("data9") UserWithParcelable data9,
                @ParameterAnno("data10") UserWithSerializable data10,
                @ParameterAnno("data11") CharSequence data11,
                @ParameterAnno("data12") Bundle data12,
                Bundle data13,
                Callback callback);

    /**
     * 测试数组
     */
    @NavigateAnno
    @PathAnno(ModuleConfig.Module1.TEST)
    void test112(Context context,
                @ParameterAnno("data1") byte[] data1,
                @ParameterAnno("data2") char[] data2,
                @ParameterAnno("data3") String[] data3,
                @ParameterAnno("data4") short[] data4,
                @ParameterAnno("data5") int[] data5,
                @ParameterAnno("data6") long[] data6,
                @ParameterAnno("data7") float[] data7,
                @ParameterAnno("data8") double[] data8,
                @ParameterAnno("data9") boolean[] data9,
                @ParameterAnno("data10") Parcelable[] data10,
                @ParameterAnno("data11") CharSequence[] data11,
                Callback callback);

    @NavigateAnno
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test113(Context context,
                                @ParameterAnno("data1") ArrayList<String> stringArrayList,
                                @ParameterAnno("data2") ArrayList<Integer> integerArrayList,
                                @ParameterAnno("data3") ArrayList<Parcelable> parcelableArrayList,
                                @ParameterAnno("data4") ArrayList<CharSequence> charSequenceArrayList,
                                Callback callback);


}
