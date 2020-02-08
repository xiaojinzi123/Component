package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.cache.ClassCache;
import com.xiaojinzi.component.error.CreateInterceptorException;

/**
 * 条件的缓存 {@link Condition}
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
public class ConditionCache {

    private ConditionCache() {
    }

    /**
     * 内部做了缓存,如果缓存中没有就会反射创建拦截器对象
     */
    @Nullable
    public static synchronized Condition getByClass(@NonNull Class<? extends Condition> tClass) {
        Condition t = ClassCache.get(tClass);
        if (t != null) {
            return t;
        }
        try {
            // 创建拦截器
            t = tClass.newInstance();
            if (t == null) {
                throw new InstantiationException("do you write default constructor or a constructor with parameter 'Application' or  a constructor with parameter 'Context' ");
            } else {
                ClassCache.put(tClass, t);
            }
        } catch (Exception e) {
            if (Component.isDebug()) {
                throw new CreateInterceptorException(e);
            }
        }
        return t;
    }

}
