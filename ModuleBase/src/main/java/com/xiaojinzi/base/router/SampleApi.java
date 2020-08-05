package com.xiaojinzi.base.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.SubParcelable;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.bean.UserWithParcelable;
import com.xiaojinzi.base.bean.UserWithSerializable;
import com.xiaojinzi.base.interceptor.DialogShowInterceptor;
import com.xiaojinzi.base.interceptor.TimeConsumingInterceptor;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.AfterActionAnno;
import com.xiaojinzi.component.anno.router.AfterErrorActionAnno;
import com.xiaojinzi.component.anno.router.AfterEventActionAnno;
import com.xiaojinzi.component.anno.router.AfterStartActionAnno;
import com.xiaojinzi.component.anno.router.BeforActionAnno;
import com.xiaojinzi.component.anno.router.BeforStartActionAnno;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.NavigateAnno;
import com.xiaojinzi.component.anno.router.OptionsAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RequestCodeAnno;
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
                   @NonNull @BeforStartActionAnno Action beforStartAction,
                   @NonNull @AfterStartActionAnno Action afterStartAction,
                   @NonNull @AfterActionAnno Action afterAction,
                   @NonNull @AfterErrorActionAnno Action afterErrorAction,
                   @NonNull @AfterEventActionAnno Action afterEventAction,
                   @NonNull Consumer<Intent> intentConsumer);

    @NavigateAnno
    @HostAnno(ModuleConfig.Module2.NAME)
    @PathAnno(ModuleConfig.Module1.TEST)
    void test1(@ParameterAnno("data") String data);

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

    @RequestCodeAnno
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
    void test8_void(Context context,
                               @ParameterAnno("data") String data,
                               BiCallback<Intent> callback);

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
    void test9(Context context, Callback callback);

    @NavigateAnno(resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    Completable test9_rx(Context context);

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    void test10(Context context, BiCallback<Intent> callback);

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test10_disposable(Context context, BiCallback<Intent> callback);

    @NavigateAnno(forIntent = true, resultCodeMatch = Activity.RESULT_OK)
    @PathAnno(ModuleConfig.Module1.TEST)
    Single<Intent> test10_rx(Context context);

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
                 @ParameterAnno("data10") UserWithParcelable[] data101,
                 @ParameterAnno("data11") CharSequence[] data11,
                 Callback callback);

    @NavigateAnno
    @PathAnno(ModuleConfig.Module1.TEST)
    NavigationDisposable test113(Context context,
                                 @ParameterAnno("data1") ArrayList<String> stringArrayList,
                                 @ParameterAnno("data2") ArrayList<Integer> integerArrayList,
                                 @ParameterAnno("data3") ArrayList<UserWithParcelable> parcelableArrayList,
                                 @ParameterAnno("data4") ArrayList<CharSequence> charSequenceArrayList,
                                 @ParameterAnno("data5") SparseArray<Parcelable> parcelableSparseArray,
                                 @ParameterAnno("data6") SparseArray<UserWithParcelable> userParcelableSparseArray,
                                 Callback callback);

    @PathAnno(ModuleConfig.Module1.TEST_INJECT2)
    void test114(Activity activity,
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
                 @ParameterAnno("data101") UserWithParcelable[] data101,
                 @ParameterAnno("data11") CharSequence[] data11,
                 @ParameterAnno("data40") String data40,
                 @ParameterAnno("data41") CharSequence data41,
                 @ParameterAnno("data42") byte data42,
                 @ParameterAnno("data43") char data43,
                 @ParameterAnno("data44") boolean data44,
                 @ParameterAnno("data45") short data45,
                 @ParameterAnno("data46") int data46,
                 @ParameterAnno("data47") long data47,
                 @ParameterAnno("data48") float data48,
                 @ParameterAnno("data49") double data49,
                 @ParameterAnno("data30") ArrayList<CharSequence> data30,
                 @ParameterAnno("data31") ArrayList<String> data31,
                 @ParameterAnno("data32") ArrayList<Integer> data32,
                 @ParameterAnno("data33") ArrayList<Parcelable> data33,
                 @ParameterAnno("data34") ArrayList<UserWithParcelable> data34,
                 @ParameterAnno("data341") ArrayList<UserWithSerializable> data341,
                 @ParameterAnno("data35") ArrayList<SubParcelable> data35,
                 @ParameterAnno("data36") SparseArray<Parcelable> data36,
                 @ParameterAnno("data37") SparseArray<UserWithParcelable> data37,
                 @ParameterAnno("data38") SparseArray<SubParcelable> data38,
                 @ParameterAnno("data12") User data12,
                 @ParameterAnno("data13") UserWithSerializable data13,
                 @ParameterAnno("data14") UserWithParcelable data14
    );


}
