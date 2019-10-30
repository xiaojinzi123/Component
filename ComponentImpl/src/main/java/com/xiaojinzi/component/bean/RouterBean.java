package com.xiaojinzi.component.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;

import java.util.Collections;
import java.util.List;

/**
 * 和注解{@link RouterAnno}是--对应的
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
@CheckClassName
public class RouterBean {

    /**
     * 可能会生成文档之类的
     */
    @Nullable
    private String desc;

    /**
     * 这个目标 Activity Class,可能为空,因为可能标记在静态方法上
     */
    @Nullable
    private Class targetClass;

    /**
     * 这个目标界面要执行的拦截器
     */
    @Nullable
    private List<Class<? extends RouterInterceptor>> interceptors;

    /**
     * 这是也是目标界面要执行的拦截器,不过这个是字符串表示的
     * 更加的跨越模块,但是寻找可能就没有上面的方式来的直接了
     *
     * @see InterceptorCenter#getByName(String)
     */
    @Nullable
    private List<String> interceptorNames;

    /**
     * 用户自定义的 {@link android.content.Intent},
     * 当标记在静态方法上并且返回值是 {@link android.content.Intent} 的时候会有值
     */
    @Nullable
    private CustomerIntentCall customerIntentCall;

    @Nullable
    public String getDesc() {
        return desc;
    }

    public void setDesc(@Nullable String desc) {
        this.desc = desc;
    }

    @Nullable
    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    @NonNull
    public List<Class<? extends RouterInterceptor>> getInterceptors() {
        if (interceptors == null) {
            return Collections.emptyList();
        }
        return interceptors;
    }

    public void setInterceptors(@Nullable List<Class<? extends RouterInterceptor>> interceptors) {
        this.interceptors = interceptors;
    }

    @NonNull
    public List<String> getInterceptorNames() {
        if (interceptorNames == null) {
            return Collections.emptyList();
        }
        return interceptorNames;
    }

    public void setInterceptorNames(@Nullable List<String> interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    @Nullable
    public CustomerIntentCall getCustomerIntentCall() {
        return customerIntentCall;
    }

    public void setCustomerIntentCall(@Nullable CustomerIntentCall customerIntentCall) {
        this.customerIntentCall = customerIntentCall;
    }

}
