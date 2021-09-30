package com.xiaojinzi.component.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import java.io.Serializable
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

/**
 * 传递参数是 Android 中的家常便事, 一般我们往 [Intent.putExtra] 各个方法中塞值. 我们称之为基础传值功能
 * 而 [ParameterSupport] 是在上述的 基础传值功能 上增加对 [Uri] 中的 [Uri.getQuery] 的支持
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
object ParameterSupport {

    /**
     * 所有query的值都会被存在 bundle 中的这个 key 对应的内置 bundle 中
     * 也就是： bundle.bundle
     */
    const val KEY_URI_QUERY_BUNDLE = "_componentQueryBundle"
    const val KEY_URI = "_componentRouterUri"

    @JvmStatic
    @JvmOverloads
    fun syncUriToBundle(uri: Uri, bundle: Bundle) {
        val routerParameterBundle = Bundle()
        val queryParameterNames = uri.queryParameterNames
        if (queryParameterNames != null) {
            for (key in queryParameterNames) {
                val values = uri.getQueryParameters(key)
                routerParameterBundle.putStringArrayList(key, ArrayList(values))
            }
        }
        bundle.putBundle(KEY_URI_QUERY_BUNDLE, routerParameterBundle)
        bundle.putString(KEY_URI, uri.toString())
    }

    @JvmStatic
    @JvmOverloads
    fun getUriIgnoreError(intent: Intent): Uri? {
        Utils.checkNullPointer(intent, "intent")
        return try {
            val uriStr = getUriAsString(intent)
            if (uriStr == null) null else Uri.parse(uriStr)
        } catch (ignore: Exception) {
            null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getUri(intent: Intent): Uri? {
        val uriStr = getUriAsString(intent)
        return if (uriStr == null) null else Uri.parse(uriStr)
    }

    @JvmStatic
    @JvmOverloads
    fun getUriIgnoreError(bundle: Bundle): Uri? {
        return try {
            val uriStr = getUriAsString(bundle)
            if (uriStr == null) null else Uri.parse(uriStr)
        } catch (ignore: Exception) {
            null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getUri(bundle: Bundle): Uri? {
        val uriStr = getUriAsString(bundle)
        return if (uriStr == null) null else Uri.parse(uriStr)
    }

    @JvmStatic
    @JvmOverloads
    fun getUriAsString(bundle: Bundle): String? {
        return bundle.getString(KEY_URI)
    }

    @JvmStatic
    @JvmOverloads
    fun getUriAsString(intent: Intent): String? {
        return intent.extras?.run {
            getUriAsString(bundle = this)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun containsKey(bundle: Bundle, key: String): Boolean {
        // 如果 query 或者普通传值中有这个 key, 就代表有这个值. 不做值的类型的判断的.
        return bundle.getBundle(KEY_URI_QUERY_BUNDLE) != null &&
                bundle.getBundle(KEY_URI_QUERY_BUNDLE)!!.containsKey(key) ||
                bundle.containsKey(key)
    }

    // ============================================================== 查询 query 的方法开始 ==============================================================

    // ============================================================== 查询 query 的方法开始 ==============================================================
    fun <T> getQuerys(bundle: Bundle?, key: String,
                      function: Function<String, T>): List<T>? {
        if (bundle == null) {
            return null
        }
        val routerParameterBundle = bundle.getBundle(KEY_URI_QUERY_BUNDLE)
                ?: return null
        // may be null
        val values: ArrayList<String>? = routerParameterBundle.getStringArrayList(key)
        return if (values.isNullOrEmpty()) {
            null
        } else try {
            val result = ArrayList<T>(values.size)
            for (value in values) {
                result.add(function.apply(value))
            }
            result
        } catch (ignore: Exception) {
            null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryString(intent: Intent, key: String,
                       defaultValue: String? = null): String? {
        return getQueryString(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryString(bundle: Bundle?, key: String,
                       defaultValue: String? = null): String? {
        val values = getQueryStrings(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryStrings(intent: Intent, key: String): List<String> {
        return getQueryStrings(intent.extras, key)!!
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryStrings(bundle: Bundle?, key: String): List<String>? {
        return getQuerys(bundle = bundle, key, object : Function<String, String> {
            @Throws(Exception::class)
            override fun apply(s: String): String {
                return s
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryInt(intent: Intent, key: String,
                    defaultValue: Int? = null): Int? {
        return getQueryInt(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryInt(bundle: Bundle?, key: String,
                    defaultValue: Int? = null): Int? {
        val values = getQueryInts(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryInts(intent: Intent, key: String): List<Int> {
        return getQueryInts(intent.extras, key)!!
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryInts(bundle: Bundle?, key: String): List<Int>? {
        return getQuerys<Int>(bundle = bundle, key, object : Function<String, Int> {
            @Throws(Exception::class)
            override fun apply(s: String): Int {
                return s.toInt()
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryLong(intent: Intent, key: String,
                     defaultValue: Long? = null): Long? {
        return getQueryLong(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryLong(bundle: Bundle?, key: String,
                     defaultValue: Long? = null): Long? {
        val values = getQueryLongs(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryLongs(intent: Intent, key: String): List<Long> {
        return getQueryLongs(intent.extras, key)!!
    }

    fun getQueryLongs(bundle: Bundle?, key: String): List<Long>? {
        return getQuerys<Long>(bundle = bundle, key, object : Function<String, Long> {
            @Throws(Exception::class)
            override fun apply(s: String): Long {
                return s.toLong()
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryDouble(intent: Intent, key: String,
                       defaultValue: Double? = null): Double? {
        return getQueryDouble(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryDouble(bundle: Bundle?, key: String,
                       defaultValue: Double? = null): Double? {
        val values = getQueryDoubles(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryDoubles(intent: Intent, key: String): List<Double> {
        return getQueryDoubles(intent.extras, key)!!
    }

    fun getQueryDoubles(bundle: Bundle?, key: String): List<Double>? {
        return getQuerys<Double>(bundle = bundle, key, object : Function<String, Double> {
            @Throws(Exception::class)
            override fun apply(s: String): Double {
                return s.toDouble()
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryFloat(intent: Intent, key: String,
                      defaultValue: Float? = null): Float? {
        return getQueryFloat(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryFloat(bundle: Bundle?, key: String,
                      defaultValue: Float? = null): Float? {
        val values = getQueryFloats(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryFloats(intent: Intent, key: String): List<Float> {
        return getQueryFloats(intent.extras, key)!!
    }

    fun getQueryFloats(bundle: Bundle?, key: String): List<Float>? {
        return getQuerys<Float>(bundle = bundle, key, object : Function<String, Float> {
            @Throws(Exception::class)
            override fun apply(s: String): Float {
                return s.toFloat()
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryBoolean(intent: Intent, key: String,
                        defaultValue: Boolean? = null): Boolean? {
        return getQueryBoolean(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryBoolean(bundle: Bundle?, key: String,
                        defaultValue: Boolean? = null): Boolean? {
        val values = getQueryBooleans(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryBooleans(intent: Intent, key: String): List<Boolean> {
        return getQueryBooleans(intent.extras, key)!!
    }

    fun getQueryBooleans(bundle: Bundle?, key: String): List<Boolean>? {
        return getQuerys<Boolean>(bundle = bundle, key, object : Function<String, Boolean> {
            @Throws(Exception::class)
            override fun apply(s: String): Boolean {
                return java.lang.Boolean.parseBoolean(s)
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryShort(intent: Intent, key: String,
                      defaultValue: Short? = null): Short? {
        return getQueryShort(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryShort(bundle: Bundle?, key: String,
                      defaultValue: Short? = null): Short? {
        val values = getQueryShorts(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryShorts(intent: Intent, key: String): List<Short> {
        return getQueryShorts(intent.extras, key)!!
    }

    fun getQueryShorts(bundle: Bundle?, key: String): List<Short>? {
        return getQuerys<Short>(bundle = bundle, key, object : Function<String, Short> {
            @Throws(Exception::class)
            override fun apply(s: String): Short {
                return s.toShort()
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryByte(intent: Intent, key: String,
                     defaultValue: Byte? = null): Byte? {
        return getQueryByte(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryByte(bundle: Bundle?, key: String,
                     defaultValue: Byte? = null): Byte? {
        val values = getQueryBytes(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryBytes(intent: Intent, key: String): List<Byte> {
        return getQueryBytes(intent.extras, key)!!
    }

    fun getQueryBytes(bundle: Bundle?, key: String): List<Byte>? {
        return getQuerys<Byte>(bundle = bundle, key, object : Function<String, Byte> {
            @Throws(Exception::class)
            override fun apply(s: String): Byte {
                return s.toByte()
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryChar(intent: Intent, key: String,
                     defaultValue: Char? = null): Char? {
        return getQueryChar(intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getQueryChar(bundle: Bundle?, key: String,
                     defaultValue: Char? = null): Char? {
        val values = getQueryChars(bundle = bundle, key)
        return values?.get(0) ?: defaultValue
    }


    fun getQueryChars(intent: Intent, key: String): List<Char> {
        return getQueryChars(intent.extras, key)!!
    }

    fun getQueryChars(bundle: Bundle?, key: String): List<Char>? {
        return getQuerys<Char>(bundle = bundle, key, object : Function<String, Char> {
            @Throws(Exception::class)
            override fun apply(s: String): Char {
                return if (s.length == 1) {
                    s[0]
                } else {
                    throw IllegalArgumentException("$s is not a Character")
                }
            }
        })
    }

    // ============================================================== 上面都是查询 query 的方法 ==============================================================

    // ============================================================== 上面都是查询 query 的方法 ==============================================================

    @JvmStatic
    @JvmOverloads
    fun getString(intent: Intent, key: String,
                  defaultValue: String? = null): String? {
        return getString(bundle = intent.extras, key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getString(bundle: Bundle?, key: String,
                  defaultValue: String? = null): String? {
        if (bundle == null) {
            return defaultValue
        }
        var value = getQueryString(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getString(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getStringArrayList(intent: Intent,
                           key: String,
                           defaultValue: ArrayList<String>? = null): ArrayList<String>? {
        return getStringArrayList(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getStringArrayList(bundle: Bundle?,
                           key: String,
                           defaultValue: ArrayList<String>? = null): ArrayList<String>? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryStrings(bundle = bundle, key)
        var value: ArrayList<String>? = if (queryValues == null) null else ArrayList(queryValues)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getStringArrayList(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getInt(intent: Intent, key: String,
               defaultValue: Int? = null): Int? {
        return getInt(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getInt(bundle: Bundle?, key: String,
               defaultValue: Int? = null): Int? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Int? = null
        // 获取 query 中的
        value = getQueryInt(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getInt(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getIntegerArrayList(intent: Intent, key: String,
                            defaultValue: ArrayList<Int>? = null): ArrayList<Int>? {
        return getIntegerArrayList(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getIntegerArrayList(bundle: Bundle?, key: String,
                            defaultValue: ArrayList<Int>? = null): ArrayList<Int>? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryInts(bundle = bundle, key)
        var value: ArrayList<Int>? = if (queryValues == null) null else ArrayList(queryValues)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getIntegerArrayList(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getLong(intent: Intent, key: String,
                defaultValue: Long? = null): Long? {
        return getLong(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getLong(bundle: Bundle?, key: String,
                defaultValue: Long? = null): Long? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Long? = null
        // 获取 query 中的
        value = getQueryLong(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getLong(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getDouble(intent: Intent, key: String,
                  defaultValue: Double? = null): Double? {
        return getDouble(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getDouble(bundle: Bundle?, key: String,
                  defaultValue: Double? = null): Double? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Double? = null
        // 获取 query 中的
        value = getQueryDouble(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getDouble(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getCharSequence(intent: Intent, key: String,
                        defaultValue: CharSequence? = null): CharSequence? {
        return getCharSequence(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getCharSequence(bundle: Bundle?, key: String,
                        defaultValue: CharSequence? = null): CharSequence? {
        if (bundle == null) {
            return defaultValue
        }
        var value: CharSequence? = null
        value = if (bundle.containsKey(key)) {
            bundle.getCharSequence(key)
        } else {
            defaultValue
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getByte(intent: Intent, key: String,
                defaultValue: Byte? = null): Byte? {
        return getByte(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getByte(bundle: Bundle?, key: String,
                defaultValue: Byte? = null): Byte? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Byte? = null
        // 获取 query 中的
        value = getQueryByte(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getByte(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getChar(intent: Intent, key: String,
                defaultValue: Char? = null): Char? {
        return getChar(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getChar(bundle: Bundle?, key: String,
                defaultValue: Char? = null): Char? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Char? = null
        // 获取 query 中的
        value = getQueryChar(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getChar(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getFloat(intent: Intent, key: String,
                 defaultValue: Float? = null): Float? {
        return getFloat(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getFloat(bundle: Bundle?, key: String,
                 defaultValue: Float? = null): Float? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Float? = null
        // 获取 query 中的
        value = getQueryFloat(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getFloat(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getShort(intent: Intent, key: String,
                 defaultValue: Short? = null): Short? {
        return getShort(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getShort(bundle: Bundle?, key: String,
                 defaultValue: Short? = null): Short? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Short? = null
        // 获取 query 中的
        value = getQueryShort(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getShort(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getBoolean(intent: Intent, key: String,
                   defaultValue: Boolean? = null): Boolean? {
        return getBoolean(intent.extras, key = key, defaultValue)
    }


    @JvmStatic
    @JvmOverloads
    fun getBoolean(bundle: Bundle?, key: String,
                   defaultValue: Boolean? = null): Boolean? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Boolean? = null
        // 获取 query 中的
        value = getQueryBoolean(bundle = bundle, key = key, null)
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getBoolean(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    // ======================================== Array 实现 ========================================

    // ======================================== Array 实现 ========================================

    @JvmStatic
    @JvmOverloads
    fun getStringArray(intent: Intent, key: String,
                       defaultValue: Array<String>? = null): Array<String>? {
        return getStringArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getStringArray(bundle: Bundle?, key: String,
                       defaultValue: Array<String>? = null): Array<String>? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryStrings(bundle = bundle, key)
        var value: Array<String>? = queryValues?.toTypedArray<String>()
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getStringArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getCharSequenceArray(intent: Intent, key: String,
                             defaultValue: Array<CharSequence>? = null): Array<CharSequence>? {
        return getCharSequenceArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getCharSequenceArray(bundle: Bundle?, key: String,
                             defaultValue: Array<CharSequence>? = null): Array<CharSequence>? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Array<CharSequence>? = null
        value = if (bundle.containsKey(key)) {
            bundle.getCharSequenceArray(key)
        } else {
            defaultValue
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getBooleanArray(intent: Intent, key: String,
                        defaultValue: BooleanArray? = null): BooleanArray? {
        return getBooleanArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getBooleanArray(bundle: Bundle?, key: String,
                        defaultValue: BooleanArray? = null): BooleanArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryBooleans(bundle = bundle, key)
        var value: BooleanArray? = null
        if (queryValues != null) {
            value = BooleanArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getBooleanArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getByteArray(intent: Intent, key: String,
                     defaultValue: ByteArray? = null): ByteArray? {
        return getByteArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getByteArray(bundle: Bundle?, key: String,
                     defaultValue: ByteArray? = null): ByteArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryBytes(bundle = bundle, key)
        var value: ByteArray? = null
        if (queryValues != null) {
            value = ByteArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getByteArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getCharArray(intent: Intent, key: String,
                     defaultValue: CharArray? = null): CharArray? {
        return getCharArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getCharArray(bundle: Bundle?, key: String,
                     defaultValue: CharArray? = null): CharArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryChars(bundle = bundle, key)
        var value: CharArray? = null
        if (queryValues != null) {
            value = CharArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getCharArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getShortArray(intent: Intent, key: String,
                      defaultValue: ShortArray? = null): ShortArray? {
        return getShortArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getShortArray(bundle: Bundle?, key: String,
                      defaultValue: ShortArray? = null): ShortArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryShorts(bundle = bundle, key)
        var value: ShortArray? = null
        if (queryValues != null) {
            value = ShortArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getShortArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getIntArray(intent: Intent, key: String,
                    defaultValue: IntArray? = null): IntArray? {
        return getIntArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getIntArray(bundle: Bundle?, key: String,
                    defaultValue: IntArray? = null): IntArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryInts(bundle = bundle, key)
        var value: IntArray? = null
        if (queryValues != null) {
            value = IntArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getIntArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getLongArray(intent: Intent, key: String,
                     defaultValue: LongArray? = null): LongArray? {
        return getLongArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getLongArray(bundle: Bundle?, key: String,
                     defaultValue: LongArray? = null): LongArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryLongs(bundle = bundle, key)
        var value: LongArray? = null
        if (queryValues != null) {
            value = LongArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getLongArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getFloatArray(intent: Intent, key: String,
                      defaultValue: FloatArray? = null): FloatArray? {
        return getFloatArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getFloatArray(bundle: Bundle?, key: String,
                      defaultValue: FloatArray? = null): FloatArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryFloats(bundle = bundle, key)
        var value: FloatArray? = null
        if (queryValues != null) {
            value = FloatArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getFloatArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getDoubleArray(intent: Intent, key: String,
                       defaultValue: DoubleArray? = null): DoubleArray? {
        return getDoubleArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getDoubleArray(bundle: Bundle?, key: String,
                       defaultValue: DoubleArray? = null): DoubleArray? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryDoubles(bundle = bundle, key)
        var value: DoubleArray? = null
        if (queryValues != null) {
            value = DoubleArray(queryValues.size)
            for (i in value.indices) {
                value[i] = queryValues[i]
            }
        }
        if (value == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getDoubleArray(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getParcelableArray(intent: Intent, key: String,
                           defaultValue: Array<Parcelable>? = null): Array<Parcelable>? {
        return getParcelableArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getParcelableArray(bundle: Bundle?, key: String,
                           defaultValue: Array<Parcelable>? = null): Array<Parcelable>? {
        if (bundle == null) {
            return defaultValue
        }
        var value: Array<Parcelable>? = null
        value = if (bundle.containsKey(key)) {
            bundle.getParcelableArray(key)
        } else {
            defaultValue
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getParcelableArrayList(intent: Intent, key: String,
                                                 defaultValue: ArrayList<T>? = null): ArrayList<T>? {
        return getParcelableArrayList(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getParcelableArrayList(bundle: Bundle?, key: String,
                                                 defaultValue: ArrayList<T>? = null): ArrayList<T>? {
        if (bundle == null) {
            return defaultValue
        }
        var value: ArrayList<T>? = null
        value = if (bundle.containsKey(key)) {
            bundle.getParcelableArrayList(key)
        } else {
            defaultValue
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getSparseParcelableArray(intent: Intent, key: String,
                                                   defaultValue: SparseArray<T>? = null): SparseArray<T>? {
        return getSparseParcelableArray(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getSparseParcelableArray(bundle: Bundle?, key: String,
                                                   defaultValue: SparseArray<T>? = null): SparseArray<T>? {
        if (bundle == null) {
            return defaultValue
        }
        var value: SparseArray<T>? = null
        value = if (bundle.containsKey(key)) {
            bundle.getSparseParcelableArray(key)
        } else {
            defaultValue
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun getCharSequenceArrayList(intent: Intent, key: String,
                                 defaultValue: ArrayList<CharSequence>? = null): ArrayList<CharSequence>? {
        return getCharSequenceArrayList(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun getCharSequenceArrayList(bundle: Bundle?, key: String,
                                 defaultValue: ArrayList<CharSequence>? = null): ArrayList<CharSequence>? {
        if (bundle == null) {
            return defaultValue
        }
        val queryValues = getQueryStrings(bundle = bundle, key)
        var value: ArrayList<CharSequence>? = if (queryValues == null) null else ArrayList(queryValues)
        if (queryValues == null) {
            value = if (bundle.containsKey(key)) {
                bundle.getCharSequenceArrayList(key)
            } else {
                defaultValue
            }
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getParcelable(intent: Intent, key: String,
                                        defaultValue: T? = null): T? {
        return getParcelable(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Parcelable> getParcelable(bundle: Bundle?, key: String,
                                        defaultValue: T? = null): T? {
        if (bundle == null) {
            return defaultValue
        }
        var value: T? = null
        value = if (bundle.containsKey(key)) {
            bundle.getParcelable(key)
        } else {
            defaultValue
        }
        return value
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Serializable> getSerializable(intent: Intent, key: String,
                                            defaultValue: T? = null): T? {
        return getSerializable(intent.extras, key = key, defaultValue)
    }

    @JvmStatic
    @JvmOverloads
    fun <T : Serializable> getSerializable(bundle: Bundle?, key: String,
                                            defaultValue: T? = null): T? {
        if (bundle == null) {
            return defaultValue
        }
        var value: T? = null
        value = if (bundle.containsKey(key)) {
            bundle.getSerializable(key) as T?
        } else {
            defaultValue
        }
        return value
    }
    
}