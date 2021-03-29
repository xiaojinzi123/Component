package com.xiaojinzi.component;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
public class ComponentConstants {

    private ComponentConstants() {
    }

    /**
     * 当使用 Fragment 或者 Context 中有这个 tag 的 fragment,那么就会最终用这个实现跳转
     */
    public static final String ACTIVITY_RESULT_FRAGMENT_TAG = "TAG_FOR_ROUTER_GET_ACTIVITY_RESULT";

    // System interface

    public static final String COMPONENT_GENERATED_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.support.ComponentGeneratedAnno";
    public static final String APPLICATION_LIFECYCLE_INTERFACE_CLASS_NAME = "com.xiaojinzi.component.application.IApplicationLifecycle";
    public static final String INTERCEPTOR_INTERFACE_CLASS_NAME = "com.xiaojinzi.component.impl.RouterInterceptor";
    public static final String ROUTERCENTER_CLASS_NAME = "com.xiaojinzi.component.impl.RouterCenter";
    public static final String ROUTERDEGRADE_CLASS_NAME = "com.xiaojinzi.component.impl.RouterDegrade";
    public static final String INTERCEPTOR_UTIL_CLASS_NAME = "com.xiaojinzi.component.support.RouterInterceptorCache";
    public static final String ROUTER_CLASS_NAME = "com.xiaojinzi.component.impl.Router";
    public static final String ROUTER_RX_CLASS_NAME = "com.xiaojinzi.component.impl.RxRouter";
    public static final String INTERCEPTOR_BEAN_CLASS_NAME = "com.xiaojinzi.component.impl.interceptor.InterceptorBean";
    public static final String CENTERINTERCEPTOR_CLASS_NAME = "com.xiaojinzi.component.impl.interceptor.InterceptorCenter";
    public static final String ROUTERDEGRADECENTER_CLASS_NAME = "com.xiaojinzi.component.impl.RouterDegradeCenter";
    public static final String SERVICE_CLASS_NAME = "com.xiaojinzi.component.impl.service.ServiceManager";
    public static final String CENTERSERVICE_CLASS_NAME = "com.xiaojinzi.component.impl.service.ServiceCenter";
    public static final String PARAMETERSUPPORT_CLASS_NAME = "com.xiaojinzi.component.support.ParameterSupport";
    public static final String INJECT_CLASS_NAME = "com.xiaojinzi.component.support.Inject";
    public static final String NAVIGATIONDISPOSABLE_CLASS_NAME = "com.xiaojinzi.component.support.NavigationDisposable";
    public static final String CALLBACK_CLASS_NAME = "com.xiaojinzi.component.impl.Callback";
    public static final String BICALLBACK_CLASS_NAME = "com.xiaojinzi.component.impl.BiCallback";
    public static final String CALL_CLASS_NAME = "com.xiaojinzi.component.impl.Call";
    public static final String NAVIGATOR_CLASS_NAME = "com.xiaojinzi.component.impl.Navigator";
    public static final String ROUTER_REQUEST_CLASS_NAME = "com.xiaojinzi.component.impl.RouterRequest";
    public static final String CONSUMER_CLASS_NAME = "com.xiaojinzi.component.support.Consumer";
    public static final String CLASSCACHE_CLASS_NAME = "com.xiaojinzi.component.cache.ClassCache";
    public static final String CONDITIONCACHE_CLASS_NAME = "com.xiaojinzi.component.support.ConditionCache";
    public static final String ROUTER_BEAN_CLASS_NAME = "com.xiaojinzi.component.bean.RouterBean";
    public static final String PAGEINTERCEPTOR_BEAN_CLASS_NAME = "com.xiaojinzi.component.bean.PageInterceptorBean";
    public static final String ROUTER_DEGRADE_BEAN_CLASS_NAME = "com.xiaojinzi.component.bean.RouterDegradeBean";
    public static final String CUSTOMER_INTENT_CALL_CLASS_NAME = "com.xiaojinzi.component.support.CustomerIntentCall";
    public static final String FRAGMENT_MANAGER_CALL_CLASS_NAME = "com.xiaojinzi.component.impl.fragment.FragmentManager";
    public static final String FRAGMENT_CENTER_CALL_CLASS_NAME = "com.xiaojinzi.component.impl.fragment.FragmentCenter";
    public static final String CALLABLE_CLASS_NAME = "com.xiaojinzi.component.support.Callable";
    public static final String SINGLETON_CALLABLE_CLASS_NAME = "com.xiaojinzi.component.support.SingletonCallable";
    public static final String DECORATOR_CALLABLE_CLASS_NAME = "com.xiaojinzi.component.support.DecoratorCallable";
    public static final String FUNCTION1_CLASS_NAME = "com.xiaojinzi.component.support.Function1";
    public static final String SINGLETON_FUNCTION1_CLASS_NAME = "com.xiaojinzi.component.support.SingletonFunction1";

    public static final String ANDROID_PARCELABLE = "android.os.Parcelable";
    public static final String ANDROID_APPLICATION = "android.app.Application";
    public static final String ANDROID_CONTEXT = "android.content.Context";
    public static final String ANDROID_FRAGMENT = "android.support.v4.app.Fragment";
    public static final String ANDROID_ACTIVITY = "android.app.Activity";
    public static final String ANDROID_INTENT = "android.content.Intent";
    public static final String ANDROID_ANNOTATION_NULLABLE = "android.support.annotation.Nullable";
    public static final String ANDROID_ANNOTATION_NONNULL = "android.support.annotation.NonNull";
    public static final String ANDROID_ANNOTATION_KEEP = "android.support.annotation.Keep";
    public static final String ANDROID_BUNDLE = "android.os.Bundle";
    public static final String ANDROID_SPARSEARRAY = "android.util.SparseArray";

    public static final String JAVA_CLASS = "java.lang.Class";
    public static final String JAVA_EXCEPTION = "java.lang.Exception";
    public static final String JAVA_STRING = "java.lang.String";
    public static final String JAVA_INTEGER = "java.lang.Integer";
    public static final String JAVA_MAP = "java.util.Map";
    public static final String JAVA_LIST = "java.util.List";
    public static final String JAVA_COLLECTIONS = "java.util.Collections";
    public static final String JAVA_ARRAYLIST = "java.util.ArrayList";
    public static final String JAVA_HASHMAP = "java.util.HashMap";
    public static final String JAVA_HASHSET = "java.util.HashSet";
    public static final String JAVA_SERIALIZABLE = "java.io.Serializable";
    public static final String JAVA_CHARSEQUENCE = "java.lang.CharSequence";

    public static final String KOTLIN_METADATA = "kotlin.Metadata";
    public static final String KOTLIN_JVMFIELD = "kotlin.jvm.JvmField";

    // RxJava 几种 Observable

    public static final String RXJAVA_COMPLETABLE = "io.reactivex.Completable";
    public static final String RXJAVA_SINGLE = "io.reactivex.Single";

    public static final String SEPARATOR = "/";

    /**
     * 默认的目标字段注入的class后缀
     */
    public static final String INJECT_SUFFIX = "_inject";

    /**
     * 整串字符串放到 https://jex.im/regulex 就可以看到效果
     */
    public static final String HOST_REGEX =
            "^([a-z]|[A-Z])([a-z]|[A-Z]|[0-9]|_)*$";

}
