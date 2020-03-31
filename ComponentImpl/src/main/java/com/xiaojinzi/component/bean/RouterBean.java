package com.xiaojinzi.component.bean;

import android.support.annotation.Nullable;

import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.CustomerIntentCall;

import java.util.ArrayList;
import java.util.List;

/**
 * 和注解{@link RouterAnno}是--对应的
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
public class RouterBean {

    /**
     * 可能会生成文档之类的
     */
    @Nullable
    private String desc;

    /**
     * 这个目标 Activity Class,可能为空, 因为可能标记在静态方法上
     */
    @Nullable
    private Class targetClass;

    private List<PageInterceptorBean> pageInterceptors = new ArrayList<>(4);

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

    public void setPageInterceptors(List<PageInterceptorBean> pageInterceptors) {
        this.pageInterceptors = pageInterceptors;
    }

    public List<PageInterceptorBean> getPageInterceptors() {
        return pageInterceptors;
    }

    @Nullable
    public CustomerIntentCall getCustomerIntentCall() {
        return customerIntentCall;
    }

    public void setCustomerIntentCall(@Nullable CustomerIntentCall customerIntentCall) {
        this.customerIntentCall = customerIntentCall;
    }

}
