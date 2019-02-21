package com.ehi.component.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 当一个跳转完成的时候,总会有数据上的携带,所以这里这个类是帮助您获取参数的值的
 * 其中会自动帮助您获取到 query 中的,如果您想单独获取 query 中的值
 * {@link QueryParameterSupport} 您可以用这个
 * 当只有 bundle 的时候,其实
 * time   : 2019/01/24
 *
 * @author : xiaojinzi 30212
 */
public class ParameterSupport {

    private ParameterSupport() {
    }

    @Nullable
    public static String getString(@NonNull Intent intent, @NonNull String key) {
        return getString(intent, key, null);
    }

    @Nullable
    public static String getString(@NonNull Intent intent, @NonNull String key, String defaultValue) {
        return getString(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static String getString(@NonNull Bundle bundle, @NonNull String key) {
        return getString(bundle, key, null);
    }

    @Nullable
    public static String getString(@NonNull Bundle bundle, @NonNull String key, String defaultValue) {
        String value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getString(bundle, key, null);
        if (value == null) {
            value = bundle.getString(key);
        }
        return value;
    }

    @Nullable
    public static Integer getInt(@NonNull Intent intent, @NonNull String key) {
        return getInt(intent, key, null);
    }

    @Nullable
    public static Integer getInt(@NonNull Intent intent, @NonNull String key, Integer defaultValue) {
        return getInt(intent.getExtras(), key, defaultValue);
    }

    public static Integer getInt(@NonNull Bundle bundle, @NonNull String key) {
        return getInt(bundle, key, null);
    }

    @Nullable
    public static Integer getInt(@NonNull Bundle bundle, @NonNull String key, Integer defaultValue) {
        Integer value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getInt(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getInt(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Long getLong(@NonNull Intent intent, @NonNull String key) {
        return getLong(intent, key, null);
    }

    @Nullable
    public static Long getLong(@NonNull Intent intent, @NonNull String key, Long defaultValue) {
        return getLong(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Long getLong(@NonNull Bundle bundle, @NonNull String key) {
        return getLong(bundle, key, null);
    }

    @Nullable
    public static Long getLong(@NonNull Bundle bundle, @NonNull String key, Long defaultValue) {
        Long value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getLong(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getLong(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Double getDouble(@NonNull Intent intent, @NonNull String key) {
        return getDouble(intent, key, null);
    }

    @Nullable
    public static Double getDouble(@NonNull Intent intent, @NonNull String key, Double defaultValue) {
        return getDouble(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Double getDouble(@NonNull Bundle bundle, @NonNull String key) {
        return getDouble(bundle, key, null);
    }

    @Nullable
    public static Double getDouble(@NonNull Bundle bundle, @NonNull String key, Double defaultValue) {
        Double value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getDouble(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getDouble(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Float getFloat(@NonNull Intent intent, @NonNull String key) {
        return getFloat(intent, key, null);
    }

    @Nullable
    public static Float getFloat(@NonNull Intent intent, @NonNull String key, Float defaultValue) {
        return getFloat(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Float getFloat(@NonNull Bundle bundle, @NonNull String key) {
        return getFloat(bundle, key, null);
    }

    @Nullable
    public static Byte getByte(@NonNull Intent intent, @NonNull String key) {
        return getByte(intent, key, null);
    }

    @Nullable
    public static Byte getByte(@NonNull Intent intent, @NonNull String key, Byte defaultValue) {
        return getByte(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Byte getByte(@NonNull Bundle bundle, @NonNull String key) {
        return getByte(bundle, key, null);
    }

    @Nullable
    public static Byte getByte(@NonNull Bundle bundle, @NonNull String key, Byte defaultValue) {
        Byte value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getByte(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getByte(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Float getFloat(@NonNull Bundle bundle, @NonNull String key, Float defaultValue) {
        Float value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getFloat(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getFloat(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Short getShort(@NonNull Intent intent, @NonNull String key) {
        return getShort(intent, key, null);
    }

    @Nullable
    public static Short getShort(@NonNull Intent intent, @NonNull String key, Short defaultValue) {
        return getShort(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Short getShort(@NonNull Bundle bundle, @NonNull String key) {
        return getShort(bundle, key, null);
    }

    @Nullable
    public static Short getShort(@NonNull Bundle bundle, @NonNull String key, Short defaultValue) {
        Short value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getShort(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getShort(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Boolean getBoolean(@NonNull Intent intent, @NonNull String key) {
        return getBoolean(intent, key, null);
    }

    @Nullable
    public static Boolean getBoolean(@NonNull Intent intent, @NonNull String key, Boolean defaultValue) {
        return getBoolean(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Boolean getBoolean(@NonNull Bundle bundle, @NonNull String key) {
        return getBoolean(bundle, key, null);
    }

    @Nullable
    public static Boolean getBoolean(@NonNull Bundle bundle, @NonNull String key, Boolean defaultValue) {
        Boolean value = null;
        // 获取 query 中的
        value = QueryParameterSupport.getBoolean(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getBoolean(key);
            }else {
                value = defaultValue;
            }
        }
        return value;
    }

}
