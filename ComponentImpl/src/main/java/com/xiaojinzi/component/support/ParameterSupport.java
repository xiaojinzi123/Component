package com.xiaojinzi.component.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 传递参数是 Android 中的家常便事, 一般我们往 {@link Intent#putExtra} 各个方法中塞值. 我们称之为基础传值功能
 * 而 {@link ParameterSupport} 是在上述的 基础传值功能 上增加对 {@link Uri} 中的 {@link Uri#getQuery()} 的支持
 * 举个例子：
 * <pre>
 *     Router.with(this)
 *           .url("router://xxx/xxx?name=xiaojinzi")
 *           .putInt("age", 11)
 *           .forward();
 * </pre>
 * 上述代码中, 有两个参数：name 和 age
 * 如果你不通过 {@link ParameterSupport} 你获取不到 name 的值. 你只能获取到 age 的值
 * 而你通过 {@link ParameterSupport#getString(Intent, String)} 就可以获取到 name 的值
 * 如果 {@link Uri} 的 query 中和 putXXX 方法的 key 相同呢？
 * <pre>
 *     Router.with(this)
 *           .url("router://xxx/xxx?name=xiaojinzi")
 *           .putInt("name", "hello")
 *           .forward();
 * </pre>
 * 这时候你通过 {@link ParameterSupport#getString(Intent, String)}
 * 根据 key = "name" 获取的话. 会得到 "xiaojinzi". 因为 query 的值的优先级比 Bundle 中的高
 * 如果 query 没有对应的值, 才会用 Bundle 中的, 比如下面的场景：
 * <pre>
 *     Router.with(this)
 *           .url("router://xxx/xxx?age=11")
 *           .putInt("name", "hello")
 *           .forward();
 * </pre>
 * 这时候你通过 {@link ParameterSupport#getString(Intent, String)}
 * 根据 key = "name" 获取的话. 会得到 "hello". 因为 query 中并没有 key = "name" 的值
 * 如果您想单独获取 query 中的值
 * {@link #getQueryBoolean(Bundle, String)} 您可以用 getQueryXXX 之类的方法单独获取 query 中的数据
 * time   : 2019/01/24
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
public class ParameterSupport {

    private ParameterSupport() {
    }

    /**
     * 所有query的值都会被存在 bundle 中的这个 key 对应的内置 bundle 中
     * 也就是： bundle.bundle
     */
    public static final String KEY_URI_QUERY_BUNDLE = "_componentQueryBundle";
    public static final String KEY_URI = "_componentRouterUri";

    public static void putQueryBundleToBundle(@NonNull Bundle bundle, @NonNull Uri uri) {
        Bundle routerParameterBundle = new Bundle();
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        if (queryParameterNames != null) {
            for (String key : queryParameterNames) {
                List<String> values = uri.getQueryParameters(key);
                routerParameterBundle.putStringArrayList(key, new ArrayList(values));
            }
        }
        bundle.putBundle(KEY_URI_QUERY_BUNDLE, routerParameterBundle);
    }

    public static void putUriStringToBundle(@NonNull Bundle bundle, @NonNull Uri uri) {
        bundle.putString(KEY_URI, uri.toString());
    }

    @Nullable
    public static Uri getUriIgnoreError(@NonNull Intent intent) {
        try {
            String uriStr = getUriAsString(intent);
            return uriStr == null ? null : Uri.parse(uriStr);
        } catch (Exception ignore) {
            return null;
        }
    }

    @Nullable
    public static Uri getUri(@NonNull Intent intent) {
        String uriStr = getUriAsString(intent);
        return uriStr == null ? null : Uri.parse(uriStr);
    }

    @Nullable
    public static Uri getUriIgnoreError(@NonNull Bundle bundle) {
        try {
            String uriStr = getUriAsString(bundle);
            return uriStr == null ? null : Uri.parse(uriStr);
        } catch (Exception ignore) {
            return null;
        }
    }

    @Nullable
    public static Uri getUri(@NonNull Bundle bundle) {
        String uriStr = getUriAsString(bundle);
        return uriStr == null ? null : Uri.parse(uriStr);
    }

    @Nullable
    public static String getUriAsString(@NonNull Bundle bundle) {
        return bundle == null ? null : bundle.getString(KEY_URI);
    }

    @Nullable
    public static String getUriAsString(@NonNull Intent intent) {
        if (intent == null) {
            return null;
        } else {
            if (intent.getExtras() == null) {
                return null;
            } else {
                return intent.getExtras().getString(KEY_URI);
            }
        }
    }

    // ============================================================== 查询 query 的方法开始 ==============================================================

    @Nullable
    public static <T> List<T> getQuerys(@Nullable Bundle bundle, @NonNull String key, @NonNull Function<String, T> function) {
        if (bundle == null) {
            return null;
        }
        Bundle routerParameterBundle = bundle.getBundle(KEY_URI_QUERY_BUNDLE);
        if (routerParameterBundle == null) {
            return null;
        }
        // may be null
        ArrayList<String> values = routerParameterBundle.getStringArrayList(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        try {
            ArrayList<T> result = new ArrayList<>(values.size());
            for (String value : values) {
                result.add(function.apply(value));
            }
            return result;
        } catch (Exception ignore) {
            return null;
        }
    }

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
        List<String> values = getQueryStrings(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }

    @NonNull
    public static List<String> getQueryStrings(@NonNull Intent intent, @NonNull String key) {
        return getQueryStrings(intent.getExtras(), key);
    }

    @Nullable
    public static List<String> getQueryStrings(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, String>() {
            @NonNull
            @Override
            public String apply(@NonNull String s) throws Exception {
                return s;
            }
        });
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
        List<Integer> values = getQueryInts(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Integer> getQueryInts(@NonNull Intent intent, @NonNull String key) {
        return getQueryInts(intent.getExtras(), key);
    }

    @Nullable
    public static List<Integer> getQueryInts(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Integer>() {
            @NonNull
            @Override
            public Integer apply(@NonNull String s) throws Exception {
                return Integer.parseInt(s);
            }
        });
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
        List<Long> values = getQueryLongs(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Long> getQueryLongs(@NonNull Intent intent, @NonNull String key) {
        return getQueryLongs(intent.getExtras(), key);
    }

    @Nullable
    public static List<Long> getQueryLongs(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Long>() {
            @NonNull
            @Override
            public Long apply(@NonNull String s) throws Exception {
                return Long.parseLong(s);
            }
        });
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
        List<Double> values = getQueryDoubles(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Double> getQueryDoubles(@NonNull Intent intent, @NonNull String key) {
        return getQueryDoubles(intent.getExtras(), key);
    }

    @Nullable
    public static List<Double> getQueryDoubles(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Double>() {
            @NonNull
            @Override
            public Double apply(@NonNull String s) throws Exception {
                return Double.parseDouble(s);
            }
        });
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
        List<Float> values = getQueryFloats(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Float> getQueryFloats(@NonNull Intent intent, @NonNull String key) {
        return getQueryFloats(intent.getExtras(), key);
    }

    @Nullable
    public static List<Float> getQueryFloats(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Float>() {
            @NonNull
            @Override
            public Float apply(@NonNull String s) throws Exception {
                return Float.parseFloat(s);
            }
        });
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
        List<Boolean> values = getQueryBooleans(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Boolean> getQueryBooleans(@NonNull Intent intent, @NonNull String key) {
        return getQueryBooleans(intent.getExtras(), key);
    }

    @Nullable
    public static List<Boolean> getQueryBooleans(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Boolean>() {
            @NonNull
            @Override
            public Boolean apply(@NonNull String s) throws Exception {
                return Boolean.parseBoolean(s);
            }
        });
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
        List<Short> values = getQueryShorts(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Short> getQueryShorts(@NonNull Intent intent, @NonNull String key) {
        return getQueryShorts(intent.getExtras(), key);
    }

    @Nullable
    public static List<Short> getQueryShorts(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Short>() {
            @NonNull
            @Override
            public Short apply(@NonNull String s) throws Exception {
                return Short.parseShort(s);
            }
        });
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
        List<Byte> values = getQueryBytes(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Byte> getQueryBytes(@NonNull Intent intent, @NonNull String key) {
        return getQueryBytes(intent.getExtras(), key);
    }

    @Nullable
    public static List<Byte> getQueryBytes(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Byte>() {
            @NonNull
            @Override
            public Byte apply(@NonNull String s) throws Exception {
                return Byte.parseByte(s);
            }
        });
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
        List<Character> values = getQueryChars(bundle, key);
        if (values == null) {
            return defaultValue;
        } else {
            return values.get(0);
        }
    }


    @NonNull
    public static List<Character> getQueryChars(@NonNull Intent intent, @NonNull String key) {
        return getQueryChars(intent.getExtras(), key);
    }

    @Nullable
    public static List<Character> getQueryChars(@Nullable Bundle bundle, @NonNull String key) {
        return getQuerys(bundle, key, new Function<String, Character>() {
            @NonNull
            @Override
            public Character apply(@NonNull String s) throws Exception {
                if (s.length() == 1) {
                    return s.charAt(0);
                } else {
                    throw new IllegalArgumentException(s + " is not a Character");
                }
            }
        });
    }

    // ============================================================== 上面都是查询 query 的方法 ==============================================================

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
        String value = getQueryString(bundle, key, null);
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
        List<String> queryValues = getQueryStrings(bundle, key);
        ArrayList<String> value = queryValues == null ? null : new ArrayList<>(queryValues);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getStringArrayList(key);
            } else {
                value = defaultValue;
            }
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
        List<Integer> queryValues = getQueryInts(bundle, key);
        ArrayList<Integer> value = queryValues == null ? null : new ArrayList<>(queryValues);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getIntegerArrayList(key);
            } else {
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
        List<String> queryValues = getQueryStrings(bundle, key);
        String[] value = queryValues == null ? null : queryValues.toArray(new String[0]);
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getStringArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Boolean> queryValues = getQueryBooleans(bundle, key);
        boolean[] value = null;
        if (queryValues != null) {
            value = new boolean[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getBooleanArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Byte> queryValues = getQueryBytes(bundle, key);
        byte[] value = null;
        if (queryValues != null) {
            value = new byte[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getByteArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Character> queryValues = getQueryChars(bundle, key);
        char[] value = null;
        if (queryValues != null) {
            value = new char[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getCharArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Short> queryValues = getQueryShorts(bundle, key);
        short[] value = null;
        if (queryValues != null) {
            value = new short[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getShortArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Integer> queryValues = getQueryInts(bundle, key);
        int[] value = null;
        if (queryValues != null) {
            value = new int[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getIntArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Long> queryValues = getQueryLongs(bundle, key);
        long[] value = null;
        if (queryValues != null) {
            value = new long[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getLongArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Float> queryValues = getQueryFloats(bundle, key);
        float[] value = null;
        if (queryValues != null) {
            value = new float[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getFloatArray(key);
            } else {
                value = defaultValue;
            }
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
        List<Double> queryValues = getQueryDoubles(bundle, key);
        double[] value = null;
        if (queryValues != null) {
            value = new double[queryValues.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = queryValues.get(i);
            }
        }
        if (value == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getDoubleArray(key);
            } else {
                value = defaultValue;
            }
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
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(@NonNull Intent intent, @NonNull String key) {
        return getParcelableArrayList(intent, key, null);
    }

    @Nullable
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(@NonNull Intent intent,
                                                                             @NonNull String key,
                                                                             ArrayList<T> defaultValue) {
        return getParcelableArrayList(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(@Nullable Bundle bundle,
                                                                             @NonNull String key) {
        return getParcelableArrayList(bundle, key, null);
    }

    @Nullable
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(@Nullable Bundle bundle,
                                                                             @NonNull String key,
                                                                             @Nullable ArrayList<T> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        ArrayList<T> value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getParcelableArrayList(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static <T extends Parcelable> SparseArray<T> getSparseParcelableArray(@NonNull Intent intent, @NonNull String key) {
        return getSparseParcelableArray(intent, key, null);
    }

    @Nullable
    public static <T extends Parcelable> SparseArray<T> getSparseParcelableArray(@NonNull Intent intent,
                                                                                 @NonNull String key,
                                                                                 SparseArray<T> defaultValue) {
        return getSparseParcelableArray(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static <T extends Parcelable> SparseArray<T> getSparseParcelableArray(@Nullable Bundle bundle,
                                                                                 @NonNull String key) {
        return getSparseParcelableArray(bundle, key, null);
    }

    @Nullable
    public static <T extends Parcelable> SparseArray<T> getSparseParcelableArray(@Nullable Bundle bundle,
                                                                                 @NonNull String key,
                                                                                 @Nullable SparseArray<T> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        SparseArray<T> value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getSparseParcelableArray(key);
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
        List<String> queryValues = getQueryStrings(bundle, key);
        ArrayList<CharSequence> value = queryValues == null ? null : new ArrayList<CharSequence>(queryValues);
        if (queryValues == null) {
            if (bundle.containsKey(key)) {
                value = bundle.getCharSequenceArrayList(key);
            } else {
                value = defaultValue;
            }
        }
        return value;
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(@NonNull Intent intent,
                                                         @NonNull String key) {
        return getParcelable(intent, key, null);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(@NonNull Intent intent,
                                                         @NonNull String key,
                                                         @Nullable T defaultValue) {
        return getParcelable(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(@Nullable Bundle bundle,
                                                         @NonNull String key) {
        return getParcelable(bundle, key, null);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(@Nullable Bundle bundle,
                                                         @NonNull String key,
                                                         @Nullable T defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        T value = null;
        if (bundle.containsKey(key)) {
            value = bundle.getParcelable(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public static <T extends  Serializable> T getSerializable(@NonNull Intent intent,
                                                         @NonNull String key) {
        return getSerializable(intent, key, null);
    }

    @Nullable
    public static <T extends  Serializable> T getSerializable(@NonNull Intent intent,
                                                         @NonNull String key,
                                                         @Nullable T defaultValue) {
        return getSerializable(intent.getExtras(), key, defaultValue);
    }

    @Nullable
    public static <T extends  Serializable> T getSerializable(@Nullable Bundle bundle,
                                                         @NonNull String key) {
        return getSerializable(bundle, key, null);
    }

    @Nullable
    public static <T extends  Serializable> T getSerializable(@Nullable Bundle bundle,
                                                             @NonNull String key,
                                                             @Nullable T defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        T value = null;
        if (bundle.containsKey(key)) {
            value = (T) bundle.getSerializable(key);
        } else {
            value = defaultValue;
        }
        return value;
    }

}
