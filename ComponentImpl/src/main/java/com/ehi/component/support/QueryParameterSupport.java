package com.ehi.component.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Uri 中的 query 参数的获取和处理,不能和 Bundle 其他数据混在一起处理,因为 url 中的 query 的参数值都是
 * String 类型的,所以每一个 getInt 之类的方法都可能为 null
 * time   : 2018/08/09
 *
 * @author : xiaojinzi 30212
 */
public class QueryParameterSupport {

    /**
     * 所有query的值都会被存在 bundle 中的这个 key 对应的内置 bundle 中
     * 也就是： bundle.bundle
     */
    public static final String KEY_BUNDLE = "RouterQueryBundle";

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
    public static Float getFloat(@NonNull Bundle bundle, @NonNull String key, Float defaultValue) {
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

}
