package com.xiaojinzi.component.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * 当一个跳转完成的时候,总会有数据上的携带,所以这里这个类是帮助您获取参数的值的
 * 其中会自动帮助您获取到 query 中的,如果您想单独获取 query 中的值
 * {@link #getQueryBoolean(Bundle, String)} 您可以用 getQueryXXX 之类的方法
 * time   : 2019/01/24
 *
 * @author : xiaojinzi 30212
 */
public class ParameterSupport {

    private ParameterSupport() {
    }

    /**
     * 所有query的值都会被存在 bundle 中的这个 key 对应的内置 bundle 中
     * 也就是： bundle.bundle
     */
    public static final String KEY_BUNDLE = "RouterQueryBundle";

    @Nullable
    public static String getQueryString(@NonNull Intent intent, @NonNull String key) {
        return getQueryString(intent, key, null);
    }

    @Nullable
    public static String getQueryString(@NonNull Intent intent, @NonNull String key, String defaultValue) {
        return getQueryString(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static String getQueryString(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryString(bundle, key, null);
    }

    @Nullable
    public static String getQueryString(@NonNull Bundle bundle, @NonNull String key, String defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Nullable
    public static Integer getQueryInt(@NonNull Intent intent, @NonNull String key) {
        return getQueryInt(intent, key, null);
    }

    @Nullable
    public static Integer getQueryInt(@NonNull Intent intent, @NonNull String key, Integer defaultValue) {
        return getQueryInt(intent.getExtras(), key, defaultValue);
    }

    public static Integer getQueryInt(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryInt(bundle, key, null);
    }

    @Nullable
    public static Integer getQueryInt(@NonNull Bundle bundle, @NonNull String key, Integer defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    @Nullable
    public static Long getQueryLong(@NonNull Intent intent, @NonNull String key) {
        return getQueryLong(intent, key, null);
    }

    @Nullable
    public static Long getQueryLong(@NonNull Intent intent, @NonNull String key, Long defaultValue) {
        return getQueryLong(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Long getQueryLong(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryLong(bundle, key, null);
    }

    @Nullable
    public static Long getQueryLong(@NonNull Bundle bundle, @NonNull String key, Long defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);

        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    @Nullable
    public static Double getQueryDouble(@NonNull Intent intent, @NonNull String key) {
        return getQueryDouble(intent, key, null);
    }

    @Nullable
    public static Double getQueryDouble(@NonNull Intent intent, @NonNull String key, Double defaultValue) {
        return getQueryDouble(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Double getQueryDouble(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryDouble(bundle, key, null);
    }

    @Nullable
    public static Double getQueryDouble(@NonNull Bundle bundle, @NonNull String key, Double defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    @Nullable
    public static Float getQueryFloat(@NonNull Intent intent, @NonNull String key) {
        return getQueryFloat(intent, key, null);
    }

    @Nullable
    public static Float getQueryFloat(@NonNull Intent intent, @NonNull String key, Float defaultValue) {
        return getQueryFloat(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Float getQueryFloat(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryFloat(bundle, key, null);
    }

    @Nullable
    public static Float getQueryFloat(@NonNull Bundle bundle, @NonNull String key, Float defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);

        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    @Nullable
    public static Boolean getQueryBoolean(@NonNull Intent intent, @NonNull String key) {
        return getQueryBoolean(intent, key, null);
    }

    @Nullable
    public static Boolean getQueryBoolean(@NonNull Intent intent, @NonNull String key, Boolean defaultValue) {
        return getQueryBoolean(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Boolean getQueryBoolean(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryBoolean(bundle, key, null);
    }

    @Nullable
    public static Boolean getQueryBoolean(@NonNull Bundle bundle, @NonNull String key, Boolean defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Boolean.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    @Nullable
    public static Short getQueryShort(@NonNull Intent intent, @NonNull String key) {
        return getQueryShort(intent, key, null);
    }

    @Nullable
    public static Short getQueryShort(@NonNull Intent intent, @NonNull String key, Short defaultValue) {
        return getQueryShort(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Short getQueryShort(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryShort(bundle, key, null);
    }

    @Nullable
    public static Short getQueryShort(@NonNull Bundle bundle, @NonNull String key, Short defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Short.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    @Nullable
    public static Byte getQueryByte(@NonNull Intent intent, @NonNull String key) {
        return getQueryByte(intent, key, null);
    }

    @Nullable
    public static Byte getQueryByte(@NonNull Intent intent, @NonNull String key, Byte defaultValue) {
        return getQueryByte(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Byte getQueryByte(@NonNull Bundle bundle, @NonNull String key) {
        return getQueryByte(bundle, key, null);
    }

    @Nullable
    public static Byte getQueryByte(@NonNull Bundle bundle, @NonNull String key, Byte defaultValue) {
        Bundle routerParameterBundle = bundle.getBundle(KEY_BUNDLE);
        if (routerParameterBundle == null) {
            return defaultValue;
        }
        // may be null
        String value = routerParameterBundle.getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Byte.valueOf(value);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    /**
     * @param bundle
     * @param uri
     * @hide
     */
    public static void putQueryBundleToBundle(@NonNull Bundle bundle, @NonNull Uri uri) {
        Bundle routerParameterBundle = new Bundle();
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        if (queryParameterNames != null) {
            for (String key : queryParameterNames) {
                String value = uri.getQueryParameter(key);
                routerParameterBundle.putString(key, value);
            }
        }
        bundle.putBundle(KEY_BUNDLE, routerParameterBundle);
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
        value = getQueryString(bundle, key, null);
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
        value = getQueryInt(bundle, key, null);
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
        value = getQueryLong(bundle, key, null);
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
        value = getQueryDouble(bundle, key, null);
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
        value = getQueryByte(bundle, key, null);
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
        value = getQueryFloat(bundle, key, null);
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
        value = getQueryShort(bundle, key, null);
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
        value = getQueryBoolean(bundle, key, null);
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
