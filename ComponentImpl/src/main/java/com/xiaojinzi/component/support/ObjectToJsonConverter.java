package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;

/**
 * Json 转化器
 */
public interface ObjectToJsonConverter {

    /**
     * 对象转 Json String
     */
    @NonNull
    String toJson(@NonNull Object obj);

}
