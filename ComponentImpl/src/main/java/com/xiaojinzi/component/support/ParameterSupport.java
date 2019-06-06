package com.xiaojinzi.component.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
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
    public static String getQueryString(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryString(bundle, key, null);
    }

    @Nullable
    public static String getQueryString(@Nullable Bundle bundle, @NonNull String key, String defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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

    public static Integer getQueryInt(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryInt(bundle, key, null);
    }

    @Nullable
    public static Integer getQueryInt(@Nullable Bundle bundle, @NonNull String key, Integer defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
    public static Long getQueryLong(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryLong(bundle, key, null);
    }

    @Nullable
    public static Long getQueryLong(@Nullable Bundle bundle, @NonNull String key, Long defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
    public static Double getQueryDouble(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryDouble(bundle, key, null);
    }

    @Nullable
    public static Double getQueryDouble(@Nullable Bundle bundle, @NonNull String key, Double defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
    public static Float getQueryFloat(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryFloat(bundle, key, null);
    }

    @Nullable
    public static Float getQueryFloat(@Nullable Bundle bundle, @NonNull String key, Float defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
    public static Boolean getQueryBoolean(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryBoolean(bundle, key, null);
    }

    @Nullable
    public static Boolean getQueryBoolean(@Nullable Bundle bundle, @NonNull String key, Boolean defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
    public static Short getQueryShort(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryShort(bundle, key, null);
    }

    @Nullable
    public static Short getQueryShort(@Nullable Bundle bundle, @NonNull String key, Short defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
    public static Byte getQueryByte(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryByte(bundle, key, null);
    }

    @Nullable
    public static Byte getQueryByte(@Nullable Bundle bundle, @NonNull String key, Byte defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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

    @Nullable
    public static Character getQueryChar(@NonNull Intent intent, @NonNull String key) {
        return getQueryChar(intent, key, null);
    }

    @Nullable
    public static Character getQueryChar(@NonNull Intent intent, @NonNull String key, Character defaultValue) {
        return getQueryChar(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Character getQueryChar(@Nullable Bundle bundle, @NonNull String key) {
        return getQueryChar(bundle, key, null);
    }

    @Nullable
    public static Character getQueryChar(@Nullable Bundle bundle, @NonNull String key, Character defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
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
            if (value.length() == 1) {
                return value.charAt(0);
            } else {
                return defaultValue;
            }
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

    // ==========================================上面都是查询 query 的方法 ==============================

    @Nullable
    public static String getString(@NonNull Intent intent, @NonNull String key) {
        return getString(intent, key, null);
    }

    @Nullable
    public static String getString(@NonNull Intent intent, @NonNull String key, String defaultValue) {
        return getString(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static String getString(@Nullable Bundle bundle, @NonNull String key) {
        return getString(bundle, key, null);
    }

    @Nullable
    public static String getString(@Nullable Bundle bundle, @NonNull String key, String defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        String value = null;
        // 获取 query 中的
        value = getQueryString(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getString(key);
            } else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static ArrayList<String> getStringArrayList(@NonNull Intent intent, @NonNull String key) {
        return getStringArrayList(intent, key, null);
    }

    @Nullable
    public static ArrayList<String> getStringArrayList(@NonNull Intent intent, @NonNull String key, ArrayList<String> defaultValue) {
        return getStringArrayList(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static ArrayList<String> getStringArrayList(@Nullable Bundle bundle, @NonNull String key) {
        return getStringArrayList(bundle, key, null);
    }

    @Nullable
    public static ArrayList<String> getStringArrayList(@Nullable Bundle bundle, @NonNull String key, @Nullable ArrayList<String> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        ArrayList<String> value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getStringArrayList(key);
        } else {
            value = defaultValue;
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

    public static Integer getInt(@Nullable Bundle bundle, @NonNull String key) {
        return getInt(bundle, key, null);
    }

    @Nullable
    public static Integer getInt(@Nullable Bundle bundle, @NonNull String key, Integer defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Integer value = null;
        // 获取 query 中的
        value = getQueryInt(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getInt(key);
            } else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static ArrayList<Integer> getIntegerArrayList(@NonNull Intent intent, @NonNull String key) {
        return getIntegerArrayList(intent, key, null);
    }

    @Nullable
    public static ArrayList<Integer> getIntegerArrayList(@NonNull Intent intent, @NonNull String key, ArrayList<Integer> defaultValue) {
        return getIntegerArrayList(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static ArrayList<Integer> getIntegerArrayList(@Nullable Bundle bundle, @NonNull String key) {
        return getIntegerArrayList(bundle, key, null);
    }

    @Nullable
    public static ArrayList<Integer> getIntegerArrayList(@Nullable Bundle bundle, @NonNull String key, @Nullable ArrayList<Integer> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        ArrayList<Integer> value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getIntegerArrayList(key);
        } else {
            value = defaultValue;
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
    public static Long getLong(@Nullable Bundle bundle, @NonNull String key) {
        return getLong(bundle, key, null);
    }

    @Nullable
    public static Long getLong(@Nullable Bundle bundle, @NonNull String key, Long defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Long value = null;
        // 获取 query 中的
        value = getQueryLong(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getLong(key);
            } else {
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
    public static Double getDouble(@Nullable Bundle bundle, @NonNull String key) {
        return getDouble(bundle, key, null);
    }

    @Nullable
    public static Double getDouble(@Nullable Bundle bundle, @NonNull String key, Double defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Double value = null;
        // 获取 query 中的
        value = getQueryDouble(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getDouble(key);
            } else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static CharSequence getCharSequence(@NonNull Intent intent, @NonNull String key) {
        return getCharSequence(intent, key, null);
    }

    @Nullable
    public static CharSequence getCharSequence(@NonNull Intent intent, @NonNull String key, CharSequence defaultValue) {
        return getCharSequence(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static CharSequence getCharSequence(@Nullable Bundle bundle, @NonNull String key) {
        return getCharSequence(bundle, key, null);
    }

    @Nullable
    public static CharSequence getCharSequence(@Nullable Bundle bundle, @NonNull String key, CharSequence defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        CharSequence value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getCharSequence(key);
        } else {
            value = defaultValue;
        }
        return value;
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
    public static Byte getByte(@Nullable Bundle bundle, @NonNull String key) {
        return getByte(bundle, key, null);
    }

    @Nullable
    public static Byte getByte(@Nullable Bundle bundle, @NonNull String key, Byte defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Byte value = null;
        // 获取 query 中的
        value = getQueryByte(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getByte(key);
            } else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static Character getChar(@NonNull Intent intent, @NonNull String key) {
        return getChar(intent, key, null);
    }

    @Nullable
    public static Character getChar(@NonNull Intent intent, @NonNull String key, Character defaultValue) {
        return getChar(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Character getChar(@Nullable Bundle bundle, @NonNull String key) {
        return getChar(bundle, key, null);
    }

    @Nullable
    public static Character getChar(@Nullable Bundle bundle, @NonNull String key, Character defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Character value = null;
        // 获取 query 中的
        value = getQueryChar(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getChar(key);
            } else {
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
    public static Float getFloat(@Nullable Bundle bundle, @NonNull String key) {
        return getFloat(bundle, key, null);
    }

    @Nullable
    public static Float getFloat(@Nullable Bundle bundle, @NonNull String key, Float defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Float value = null;
        // 获取 query 中的
        value = getQueryFloat(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getFloat(key);
            } else {
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
    public static Short getShort(@Nullable Bundle bundle, @NonNull String key) {
        return getShort(bundle, key, null);
    }

    @Nullable
    public static Short getShort(@Nullable Bundle bundle, @NonNull String key, Short defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Short value = null;
        // 获取 query 中的
        value = getQueryShort(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getShort(key);
            } else {
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
    public static Boolean getBoolean(@Nullable Bundle bundle, @NonNull String key) {
        return getBoolean(bundle, key, null);
    }

    @Nullable
    public static Boolean getBoolean(@Nullable Bundle bundle, @NonNull String key, Boolean defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Boolean value = null;
        // 获取 query 中的
        value = getQueryBoolean(bundle, key, null);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getBoolean(key);
            } else {
                value = defaultValue;
            }
        }
        return value;
    }

    // ======================================== Array 实现 ========================================

    @Nullable
    public static String[] getStringArray(@NonNull Intent intent, @NonNull String key) {
        return getStringArray(intent, key, null);
    }

    @Nullable
    public static String[] getStringArray(@NonNull Intent intent, @NonNull String key, String[] defaultValue) {
        return getStringArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static String[] getStringArray(@Nullable Bundle bundle, @NonNull String key) {
        return getStringArray(bundle, key, null);
    }

    @Nullable
    public static String[] getStringArray(@Nullable Bundle bundle, @NonNull String key, String[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        String[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getStringArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static CharSequence[] getCharSequenceArray(@NonNull Intent intent, @NonNull String key) {
        return getCharSequenceArray(intent, key, null);
    }

    @Nullable
    public static CharSequence[] getCharSequenceArray(@NonNull Intent intent, @NonNull String key, CharSequence[] defaultValue) {
        return getCharSequenceArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static CharSequence[] getCharSequenceArray(@Nullable Bundle bundle, @NonNull String key) {
        return getCharSequenceArray(bundle, key, null);
    }

    @Nullable
    public static CharSequence[] getCharSequenceArray(@Nullable Bundle bundle, @NonNull String key, CharSequence[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        CharSequence[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getCharSequenceArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static boolean[] getBooleanArray(@NonNull Intent intent, @NonNull String key) {
        return getBooleanArray(intent, key, null);
    }

    @Nullable
    public static boolean[] getBooleanArray(@NonNull Intent intent, @NonNull String key, boolean[] defaultValue) {
        return getBooleanArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static boolean[] getBooleanArray(@Nullable Bundle bundle, @NonNull String key) {
        return getBooleanArray(bundle, key, null);
    }

    @Nullable
    public static boolean[] getBooleanArray(@Nullable Bundle bundle, @NonNull String key, boolean[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        boolean[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getBooleanArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static byte[] getByteArray(@NonNull Intent intent, @NonNull String key) {
        return getByteArray(intent, key, null);
    }

    @Nullable
    public static byte[] getByteArray(@NonNull Intent intent, @NonNull String key, byte[] defaultValue) {
        return getByteArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static byte[] getByteArray(@Nullable Bundle bundle, @NonNull String key) {
        return getByteArray(bundle, key, null);
    }

    @Nullable
    public static byte[] getByteArray(@Nullable Bundle bundle, @NonNull String key, byte[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        byte[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getByteArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static char[] getCharArray(@NonNull Intent intent, @NonNull String key) {
        return getCharArray(intent, key, null);
    }

    @Nullable
    public static char[] getCharArray(@NonNull Intent intent, @NonNull String key, char[] defaultValue) {
        return getCharArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static char[] getCharArray(@Nullable Bundle bundle, @NonNull String key) {
        return getCharArray(bundle, key, null);
    }

    @Nullable
    public static char[] getCharArray(@Nullable Bundle bundle, @NonNull String key, @Nullable char[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        char[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getCharArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static short[] getShortArray(@NonNull Intent intent, @NonNull String key) {
        return getShortArray(intent, key, null);
    }

    @Nullable
    public static short[] getShortArray(@NonNull Intent intent, @NonNull String key, short[] defaultValue) {
        return getShortArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static short[] getShortArray(@Nullable Bundle bundle, @NonNull String key) {
        return getShortArray(bundle, key, null);
    }

    @Nullable
    public static short[] getShortArray(@Nullable Bundle bundle, @NonNull String key, @Nullable short[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        short[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getShortArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static int[] getIntArray(@NonNull Intent intent, @NonNull String key) {
        return getIntArray(intent, key, null);
    }

    @Nullable
    public static int[] getIntArray(@NonNull Intent intent, @NonNull String key, int[] defaultValue) {
        return getIntArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static int[] getIntArray(@Nullable Bundle bundle, @NonNull String key) {
        return getIntArray(bundle, key, null);
    }

    @Nullable
    public static int[] getIntArray(@Nullable Bundle bundle, @NonNull String key, @Nullable int[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        int[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getIntArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static long[] getLongArray(@NonNull Intent intent, @NonNull String key) {
        return getLongArray(intent, key, null);
    }

    @Nullable
    public static long[] getLongArray(@NonNull Intent intent, @NonNull String key, long[] defaultValue) {
        return getLongArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static long[] getLongArray(@Nullable Bundle bundle, @NonNull String key) {
        return getLongArray(bundle, key, null);
    }

    @Nullable
    public static long[] getLongArray(@Nullable Bundle bundle, @NonNull String key, @Nullable long[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        long[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getLongArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static float[] getFloatArray(@NonNull Intent intent, @NonNull String key) {
        return getFloatArray(intent, key, null);
    }

    @Nullable
    public static float[] getFloatArray(@NonNull Intent intent, @NonNull String key, float[] defaultValue) {
        return getFloatArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static float[] getFloatArray(@Nullable Bundle bundle, @NonNull String key) {
        return getFloatArray(bundle, key, null);
    }

    @Nullable
    public static float[] getFloatArray(@Nullable Bundle bundle, @NonNull String key, @Nullable float[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        float[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getFloatArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static double[] getDoubleArray(@NonNull Intent intent, @NonNull String key) {
        return getDoubleArray(intent, key, null);
    }

    @Nullable
    public static double[] getDoubleArray(@NonNull Intent intent, @NonNull String key, double[] defaultValue) {
        return getDoubleArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static double[] getDoubleArray(@Nullable Bundle bundle, @NonNull String key) {
        return getDoubleArray(bundle, key, null);
    }

    @Nullable
    public static double[] getDoubleArray(@Nullable Bundle bundle, @NonNull String key, @Nullable double[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        double[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getDoubleArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static Parcelable[] getParcelableArray(@NonNull Intent intent, @NonNull String key) {
        return getParcelableArray(intent, key, null);
    }

    @Nullable
    public static Parcelable[] getParcelableArray(@NonNull Intent intent, @NonNull String key, Parcelable[] defaultValue) {
        return getParcelableArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static Parcelable[] getParcelableArray(@Nullable Bundle bundle, @NonNull String key) {
        return getParcelableArray(bundle, key, null);
    }

    @Nullable
    public static Parcelable[] getParcelableArray(@Nullable Bundle bundle, @NonNull String key, @Nullable Parcelable[] defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        Parcelable[] value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getParcelableArray(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static ArrayList<Parcelable> getParcelableArrayList(@NonNull Intent intent, @NonNull String key) {
        return getParcelableArrayList(intent, key, null);
    }

    @Nullable
    public static ArrayList<Parcelable> getParcelableArrayList(@NonNull Intent intent, @NonNull String key, ArrayList<Parcelable> defaultValue) {
        return getParcelableArrayList(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static ArrayList<Parcelable> getParcelableArrayList(@Nullable Bundle bundle, @NonNull String key) {
        return getParcelableArrayList(bundle, key, null);
    }

    @Nullable
    public static ArrayList<Parcelable> getParcelableArrayList(@Nullable Bundle bundle, @NonNull String key, @Nullable ArrayList<Parcelable> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        ArrayList<Parcelable> value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getParcelableArrayList(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static ArrayList<CharSequence> getCharSequenceArrayList(@NonNull Intent intent, @NonNull String key) {
        return getCharSequenceArrayList(intent, key, null);
    }

    @Nullable
    public static ArrayList<CharSequence> getCharSequenceArrayList(@NonNull Intent intent, @NonNull String key, ArrayList<CharSequence> defaultValue) {
        return getCharSequenceArrayList(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static ArrayList<CharSequence> getCharSequenceArrayList(@Nullable Bundle bundle, @NonNull String key) {
        return getCharSequenceArrayList(bundle, key, null);
    }

    @Nullable
    public static ArrayList<CharSequence> getCharSequenceArrayList(@Nullable Bundle bundle, @NonNull String key, @Nullable ArrayList<CharSequence> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        ArrayList<CharSequence> value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getCharSequenceArrayList(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

}
