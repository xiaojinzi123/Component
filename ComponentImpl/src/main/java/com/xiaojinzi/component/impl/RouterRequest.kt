package com.xiaojinzi.component.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.SparseArray
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.Component.requiredConfig
import com.xiaojinzi.component.ComponentActivityStack.getTopAliveActivity
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.support.Action
import com.xiaojinzi.component.support.Consumer
import com.xiaojinzi.component.support.ParameterSupport
import com.xiaojinzi.component.support.Utils
import java.io.Serializable
import java.util.*

/**
 * 表示路由的一个请求类,构建时候如果参数不对是有异常会发生的,使用的时候注意这一点
 * 但是在拦截器 [RouterInterceptor] 中构建是不用关心错误的,
 * 因为拦截器的 [RouterInterceptor.intercept] 方法
 * 允许抛出异常
 *
 *
 * time   : 2018/11/29
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
class RouterRequest private constructor(builder: Builder) {

    companion object {
        const val KEY_SYNC_URI = "_componentSyncUri"
    }

    @JvmField
    val context: Context? = builder.context

    @JvmField
    val fragment: Fragment? = builder.fragment

    /**
     * 这是一个很重要的参数, 一定不可以为空,如果这个为空了,一定不能继续下去,因为很多地方直接使用这个参数的,不做空判断的
     * 而且这个参数不可以为空的
     */
    @JvmField
    val uri: Uri = builder.buildURI()

    /**
     * requestCode
     */
    @JvmField
    val requestCode: Int? = builder.requestCode

    /**
     * 框架是否帮助用户跳转拿 [com.xiaojinzi.component.bean.ActivityResult]
     * 有 requestCode 只能说明用户使用了某一个 requestCode,
     * 会调用 [Activity.startActivityForResult].
     * 但是不代表需要框架去帮你获取到 [com.xiaojinzi.component.bean.ActivityResult].
     * 所以这个值就是标记是否需要框架帮助您去获取 [com.xiaojinzi.component.bean.ActivityResult]
     */
    @JvmField
    val isForResult: Boolean = builder.isForResult

    /**
     * 是否是为了目标 Intent 来的
     */
    @JvmField
    val isForTargetIntent: Boolean = builder.isForTargetIntent

    /**
     * 跳转的时候 options 参数
     */
    @JvmField
    val options: Bundle? = builder.options

    /**
     * Intent 的 flag, 集合不可更改
     */
    @JvmField
    val intentFlags: List<Int> = Collections.unmodifiableList(builder.intentFlags)

    /**
     * Intent 的 类别, 集合不可更改
     */
    @JvmField
    val intentCategories: List<String> = Collections.unmodifiableList(builder.intentCategories)

    @JvmField
    val bundle: Bundle = Bundle().apply {
        this.putAll(builder.bundle)
    }

    @JvmField
    val intentConsumer: Consumer<Intent>? = builder.intentConsumer

    /**
     * 这个 [Action] 是在路由开始的时候调用的.
     * 和 [Activity.startActivity] 不是连着执行的.
     * 中间 post 到主线程的操作
     */
    @JvmField
    val beforeAction: Action? = builder.beforeAction

    /**
     * 这个 [Action] 是在 [Activity.startActivity] 之前调用的.
     * 和 [Activity.startActivity] 是连着执行的.
     */
    @JvmField
    val beforeStartAction: Action? = builder.beforeStartAction

    /**
     * 这个 [Action] 是在 [Activity.startActivity] 之后调用的.
     * 和 [Activity.startActivity] 是连着执行的.
     */
    @JvmField
    val afterStartAction: Action? = builder.afterStartAction

    /**
     * 这个 [Action] 是在结束之后调用的.
     * 和 [Activity.startActivity] 不是连着执行的.
     * 是在 [RouterInterceptor.Callback.onSuccess]
     * 方法中 post 到主线程完成的
     */
    @JvmField
    val afterAction: Action? = builder.afterAction

    /**
     * 这个 [Action] 是在结束之后调用的.
     * 和 [Activity.startActivity] 不是连着执行的.
     * 是在 [RouterInterceptor.Callback.onError]
     * 方法中 post 到主线程完成的
     */
    @JvmField
    val afterErrorAction: Action? = builder.afterErrorAction

    /**
     * 这个 [Action] 是在结束之后调用的.
     * 和 [Activity.startActivity] 不是连着执行的.
     * 是在 [RouterInterceptor.Callback.onSuccess] 或者
     * [RouterInterceptor.Callback.onError]
     * 方法中 post 到主线程完成的
     */
    @JvmField
    val afterEventAction: Action? = builder.afterEventAction

    /**
     * 同步 Query 到 Bundle 中
     */
    fun syncUriToBundle() {
        // 如果 URI 没有变化就不同步了
        if (bundle.getInt(KEY_SYNC_URI) == uri.hashCode()) {
            return
        }
        ParameterSupport.syncUriToBundle(uri, bundle)
        // 更新新的 hashCode
        bundle.putInt(KEY_SYNC_URI, uri.hashCode())
    }// 如果是 Activity 并且已经销毁了返回 null// 如果不是 Activity 可能是 Application,所以直接返回

    /**
     * 从 [Fragment] 和 [Context] 中获取上下文
     *
     *
     * 参数中的 [RouterRequest.context] 可能是一个 [android.app.Application] 或者是一个
     * [android.content.ContextWrapper] 或者是一个 [Activity]
     * 无论参数的类型是哪种, 此方法的返回值就只有两种类型：
     * 1. [android.app.Application]
     * 2. [Activity]
     *
     *
     * 如果返回的是 [Activity] 的 [Context],
     * 当 [Activity] 销毁了就会返回 null
     * 另外就是返回 [android.app.Application]
     *
     * @return [Context], 可能为 null, null 就只有一种情况就是界面销毁了.
     * 构建 [RouterRequest] 的时候已经保证了
     */
    val rawContext: Context?
        get() {
            var rawContext: Context? = null
            if (context != null) {
                rawContext = context
            } else if (fragment != null) {
                rawContext = fragment.context
            }
            val rawAct = Utils.getActivityFromContext(rawContext)
            // 如果不是 Activity 可能是 Application,所以直接返回
            return if (rawAct == null) {
                rawContext
            } else {
                // 如果是 Activity 并且已经销毁了返回 null
                if (Utils.isActivityDestoryed(rawAct)) {
                    null
                } else {
                    rawContext
                }
            }
        }

    /**
     * 从 [Context] 中获取 [Activity], [Context] 可能是 [android.content.ContextWrapper]
     *
     * @return 如果 Activity 销毁了就会返回 null
     */
    val activity: Activity?
        get() {
            if (context == null) {
                return null
            }
            val realActivity = Utils.getActivityFromContext(context)
                    ?: return null
            return if (Utils.isActivityDestoryed(realActivity)) {
                null
            } else realActivity
        }

    /**
     * 从参数 [Fragment] 和 [Context] 获取 Activity,
     *
     * @return 如果 activity 已经销毁并且 fragment 销毁了就会返回 null
     */
    val rawActivity: Activity?
        get() {
            var rawActivity = activity
            if (rawActivity == null) {
                if (fragment != null) {
                    rawActivity = fragment.activity
                }
            }
            if (rawActivity == null) {
                return null
            }
            return if (Utils.isActivityDestoryed(rawActivity)) {
                null
            } else {
                // 如果不是为空返回的, 那么必定不是销毁的
                rawActivity
            }
        }

    /**
     * 首先调用 [.getRawActivity] 尝试获取此次用户传入的 Context 中是否有关联的 Activity
     * 如果为空, 则尝试获取运行中的所有 Activity 中顶层的那个
     */
    val rawOrTopActivity: Activity?
        get() {
            var result = rawActivity
            if (result == null) {
                // 如果不是为空返回的, 那么必定不是销毁的
                result = getTopAliveActivity()
            }
            return result
        }

    /**
     * 这里转化的对象会比 [Builder] 对象中的参数少一个 [Builder.url]
     * 因为 uri 转化为了 scheme,host,path,queryMap 那么这时候就不需要 url 了
     */
    fun toBuilder(): Builder {
        val builder = Builder()
        // 有关界面的两个
        builder.fragment = fragment
        builder.context = context

        // 还原一个 Uri 为各个零散的参数
        builder.scheme = uri.scheme
        builder.host = uri.host
        builder.path = uri.path
        val queryParameterNames = uri.queryParameterNames
        if (queryParameterNames != null) {
            for (queryParameterName in queryParameterNames) {
                builder.queryMap[queryParameterName!!] = uri.getQueryParameter(queryParameterName)!!
            }
        }
        builder.bundle.putAll(bundle)
        builder.requestCode = requestCode
        builder.isForResult = isForResult
        builder.isForTargetIntent = isForTargetIntent
        builder.options = options
        // 这里需要新创建一个是因为不可修改的集合不可以给别人
        builder.intentCategories = ArrayList(intentCategories)
        builder.intentFlags = ArrayList(intentFlags)
        builder.intentConsumer = intentConsumer
        builder.beforeAction = beforeAction
        builder.beforeStartAction = beforeStartAction
        builder.afterStartAction = afterStartAction
        builder.afterAction = afterAction
        builder.afterErrorAction = afterErrorAction
        builder.afterEventAction = afterEventAction
        return builder
    }

    /**
     * 构建一个路由请求对象 [RouterRequest] 对象的 Builder
     *
     * @author xiaojinzi
     */
    open class Builder : URIBuilder() {

        var options: Bundle? = null

        /**
         * Intent 的 flag,允许修改的
         */
        var intentFlags: MutableList<Int> = ArrayList(2)

        /**
         * Intent 的 类别,允许修改的
         */
        var intentCategories: MutableList<String> = ArrayList(2)

        var bundle: Bundle = Bundle()
        var context: Context? = null
        var fragment: Fragment? = null
        var requestCode: Int? = null

        /**
         * 是否是跳转拿 [com.xiaojinzi.component.bean.ActivityResult] 的
         */
        var isForResult = false

        /**
         * 是否是为了目标 Intent
         */
        var isForTargetIntent = false

        var intentConsumer: Consumer<Intent>? = null

        /**
         * 路由开始之前
         */
        var beforeAction: Action? = null

        /**
         * 执行 [Activity.startActivity] 之前
         */
        var beforeStartAction: Action? = null

        /**
         * 执行 [Activity.startActivity] 之后
         */
        var afterStartAction: Action? = null

        /**
         * 跳转成功之后的 Callback
         * 此时的跳转成功仅代表目标界面启动成功, 不代表跳转拿数据的回调被回调了
         * 假如你是跳转拿数据的, 当你跳转到 A 界面, 此回调就会回调了,
         * 当你拿到 Intent 的回调了, 和此回调已经没关系了
         */
        var afterAction: Action? = null

        /**
         * 跳转失败之后的 Callback
         */
        var afterErrorAction: Action? = null

        /**
         * 跳转成功和失败之后的 Callback
         */
        var afterEventAction: Action? = null

        fun context(context: Context): Builder {
            this.context = context
            return this
        }

        fun fragment(fragment: Fragment): Builder {
            this.fragment = fragment
            return this
        }

        open fun beforeAction(@UiThread action: Action?): Builder {
            beforeAction = action
            return this
        }

        open fun beforeStartAction(@UiThread action: Action?): Builder {
            beforeStartAction = action
            return this
        }

        open fun afterStartAction(@UiThread action: Action?): Builder {
            afterStartAction = action
            return this
        }

        open fun afterAction(@UiThread action: Action?): Builder {
            afterAction = action
            return this
        }

        open fun afterErrorAction(@UiThread action: Action?): Builder {
            afterErrorAction = action
            return this
        }

        open fun afterEventAction(@UiThread action: Action?): Builder {
            afterEventAction = action
            return this
        }

        open fun requestCode(requestCode: Int?): Builder {
            this.requestCode = requestCode
            return this
        }

        /**
         * 当不是自定义跳转的时候, Intent 由框架生成,所以可以回调这个接口
         * 当自定义跳转,这个回调不会回调的,这是需要注意的点
         *
         *
         * 其实目标界面可以完全的自定义路由,这个功能实际上没有存在的必要,因为你可以为同一个界面添加上多个 [com.xiaojinzi.component.anno.RouterAnno]
         * 然后每一个 [com.xiaojinzi.component.anno.RouterAnno] 都可以有不同的行为.是可以完全的代替 [RouterRequest.intentConsumer] 方法的
         *
         * @param intentConsumer Intent 是框架自动构建完成的,里面有跳转需要的所有参数和数据,这里就是给用户一个
         * 更改的机会,最好别更改内部的参数等的信息,这里提供出来其实主要是可以让你调用Intent
         * 的 [Intent.addFlags] 等方法,并不是给你修改内部的 bundle 的
         */
        open fun intentConsumer(@UiThread intentConsumer: Consumer<Intent>?): Builder {
            this.intentConsumer = intentConsumer
            return this
        }

        open fun addIntentFlags(vararg flags: Int): Builder {
            intentFlags.addAll(flags.toList())
            return this
        }

        open fun addIntentCategories(vararg categories: String): Builder {
            intentCategories.addAll(listOf(*categories))
            return this
        }

        /**
         * 用于 API >= 16 的时候,调用 [Activity.startActivity]
         */
        open fun options(options: Bundle?): Builder {
            this.options = options
            return this
        }

        override fun url(url: String): Builder {
            super.url(url)
            return this
        }

        override fun scheme(scheme: String): Builder {
            super.scheme(scheme)
            return this
        }

        override fun hostAndPath(hostAndPath: String): Builder {
            super.hostAndPath(hostAndPath)
            return this
        }

        override fun userInfo(userInfo: String): Builder {
            super.userInfo(userInfo)
            return this
        }

        override fun host(host: String): Builder {
            super.host(host)
            return this
        }

        override fun path(path: String): Builder {
            super.path(path)
            return this
        }

        open fun putAll(bundle: Bundle): Builder {
            Utils.checkNullPointer(bundle, "bundle")
            this.bundle.putAll(bundle)
            return this
        }

        open fun putBundle(key: String, bundle: Bundle?): Builder {
            this.bundle.putBundle(key, bundle)
            return this
        }

        open fun putCharSequence(key: String, value: CharSequence?): Builder {
            this.bundle.putCharSequence(key, value)
            return this
        }

        open fun putCharSequenceArray(key: String, value: Array<CharSequence>?): Builder {
            this.bundle.putCharSequenceArray(key, value)
            return this
        }

        open fun putCharSequenceArrayList(key: String, value: ArrayList<CharSequence>?): Builder {
            this.bundle.putCharSequenceArrayList(key, value)
            return this
        }

        open fun putByte(key: String, value: Byte): Builder {
            this.bundle.putByte(key, value)
            return this
        }

        open fun putByteArray(key: String, value: ByteArray?): Builder {
            this.bundle.putByteArray(key, value)
            return this
        }

        open fun putChar(key: String, value: Char): Builder {
            this.bundle.putChar(key, value)
            return this
        }

        open fun putCharArray(key: String, value: CharArray?): Builder {
            this.bundle.putCharArray(key, value)
            return this
        }

        open fun putBoolean(key: String, value: Boolean): Builder {
            this.bundle.putBoolean(key, value)
            return this
        }

        open fun putBooleanArray(key: String, value: BooleanArray?): Builder {
            this.bundle.putBooleanArray(key, value)
            return this
        }

        open fun putString(key: String, value: String?): Builder {
            this.bundle.putString(key, value)
            return this
        }

        open fun putStringArray(key: String, value: Array<String>?): Builder {
            this.bundle.putStringArray(key, value)
            return this
        }

        open fun putStringArrayList(key: String, value: ArrayList<String>?): Builder {
            this.bundle.putStringArrayList(key, value)
            return this
        }

        open fun putShort(key: String, value: Short): Builder {
            this.bundle.putShort(key, value)
            return this
        }

        open fun putShortArray(key: String, value: ShortArray?): Builder {
            this.bundle.putShortArray(key, value)
            return this
        }

        open fun putInt(key: String, value: Int): Builder {
            this.bundle.putInt(key, value)
            return this
        }

        open fun putIntArray(key: String, value: IntArray?): Builder {
            this.bundle.putIntArray(key, value)
            return this
        }

        open fun putIntegerArrayList(key: String, value: ArrayList<Int>?): Builder {
            this.bundle.putIntegerArrayList(key, value)
            return this
        }

        open fun putLong(key: String, value: Long): Builder {
            this.bundle.putLong(key, value)
            return this
        }

        open fun putLongArray(key: String, value: LongArray?): Builder {
            this.bundle.putLongArray(key, value)
            return this
        }

        open fun putFloat(key: String, value: Float): Builder {
            this.bundle.putFloat(key, value)
            return this
        }

        open fun putFloatArray(key: String, value: FloatArray?): Builder {
            this.bundle.putFloatArray(key, value)
            return this
        }

        open fun putDouble(key: String, value: Double): Builder {
            this.bundle.putDouble(key, value)
            return this
        }

        open fun putDoubleArray(key: String, value: DoubleArray?): Builder {
            this.bundle.putDoubleArray(key, value)
            return this
        }

        open fun putParcelable(key: String, value: Parcelable?): Builder {
            this.bundle.putParcelable(key, value)
            return this
        }

        open fun putParcelableArray(key: String, value: Array<Parcelable>?): Builder {
            this.bundle.putParcelableArray(key, value)
            return this
        }

        open fun putParcelableArrayList(key: String, value: ArrayList<out Parcelable>?): Builder {
            this.bundle.putParcelableArrayList(key, value)
            return this
        }

        open fun putSparseParcelableArray(key: String, value: SparseArray<out Parcelable>?): Builder {
            this.bundle.putSparseParcelableArray(key, value)
            return this
        }

        open fun putSerializable(key: String, value: Serializable?): Builder {
            this.bundle.putSerializable(key, value)
            return this
        }

        override fun query(queryName: String, queryValue: String): Builder {
            super.query(queryName, queryValue)
            return this
        }

        override fun query(queryName: String, queryValue: Boolean): Builder {
            super.query(queryName, queryValue)
            return this
        }

        override fun query(queryName: String, queryValue: Byte): Builder {
            super.query(queryName, queryValue)
            return this
        }

        override fun query(queryName: String, queryValue: Int): Builder {
            super.query(queryName, queryValue)
            return this
        }

        override fun query(queryName: String, queryValue: Float): Builder {
            super.query(queryName, queryValue)
            return this
        }

        override fun query(queryName: String, queryValue: Long): Builder {
            super.query(queryName, queryValue)
            return this
        }

        override fun query(queryName: String, queryValue: Double): Builder {
            super.query(queryName, queryValue)
            return this
        }

        /**
         * 构建请求对象,这个构建是必须的,不能错误的,如果出错了,直接崩溃掉,因为连最基本的信息都不全没法进行下一步的操作
         *
         * @return 可能会抛出一个运行时异常, 由于您的参数在构建 uri 的时候出现的异常
         */
        open fun build(): RouterRequest {
            return RouterRequest(this)
        }
    }

    /**
     * 构造 URI 和 URL 的Builder
     *
     * @author xiaojinzi
     */
    open class URIBuilder {

        var url: String? = null
        var scheme: String? = null
        private var userInfo: String? = null
        var host: String? = null
        var path: String? = null
        var queryMap: MutableMap<String, String> = HashMap()

        open fun url(url: String): URIBuilder {
            Utils.checkStringNullPointer(url, "url")
            this.url = url
            return this
        }

        open fun scheme(scheme: String): URIBuilder {
            Utils.checkStringNullPointer(scheme, "scheme")
            this.scheme = scheme
            return this
        }

        /**
         * xxx/xxx
         *
         * @param hostAndPath xxx/xxx
         */
        open fun hostAndPath(hostAndPath: String): URIBuilder {
            Utils.checkNullPointer(hostAndPath, "hostAndPath")
            val index = hostAndPath.indexOf("/")
            if (index > 0) {
                host(hostAndPath.substring(0, index))
                path(hostAndPath.substring(index + 1))
            } else {
                Utils.debugThrowException(IllegalArgumentException("$hostAndPath is invalid"))
            }
            return this
        }

        open fun userInfo(userInfo: String): URIBuilder {
            Utils.checkStringNullPointer(userInfo, "userInfo")
            this.userInfo = userInfo
            return this
        }

        open fun host(host: String): URIBuilder {
            Utils.checkStringNullPointer(host, "host")
            this.host = host
            return this
        }

        open fun path(path: String): URIBuilder {
            Utils.checkStringNullPointer(path, "path")
            this.path = path
            return this
        }

        open fun query(queryName: String, queryValue: String): URIBuilder {
            Utils.checkStringNullPointer(queryName, "queryName")
            Utils.checkStringNullPointer(queryValue, "queryValue")
            queryMap[queryName] = queryValue
            return this
        }

        open fun query(queryName: String, queryValue: Boolean): URIBuilder {
            return query(queryName, queryValue.toString())
        }

        open fun query(queryName: String, queryValue: Byte): URIBuilder {
            return query(queryName, queryValue.toString())
        }

        open fun query(queryName: String, queryValue: Int): URIBuilder {
            return query(queryName, queryValue.toString())
        }

        open fun query(queryName: String, queryValue: Float): URIBuilder {
            return query(queryName, queryValue.toString())
        }

        open fun query(queryName: String, queryValue: Long): URIBuilder {
            return query(queryName, queryValue.toString())
        }

        open fun query(queryName: String, queryValue: Double): URIBuilder {
            return query(queryName, queryValue.toString())
        }

        /**
         * 构建一个 [Uri],如果构建失败会抛出异常
         */
        fun buildURI(): Uri {
            val builder = this
            var result: Uri?
            if (builder.url == null) {
                val uriBuilder = Uri.Builder()
                val authoritySB = StringBuffer()
                if (!userInfo.isNullOrEmpty()) {
                    authoritySB
                            .append(Uri.encode(userInfo))
                            .append("@")
                }
                authoritySB.append(
                        Uri.encode(
                                Utils.checkStringNullPointer(
                                        builder.host, "host",
                                        "do you forget call host() to set host?"
                                )
                        )
                )
                uriBuilder
                        .scheme(if (TextUtils.isEmpty(builder.scheme)) requiredConfig().defaultScheme else builder.scheme) // host 一定不能为空
                        .encodedAuthority(authoritySB.toString())
                        .path(
                                Utils.checkStringNullPointer(
                                        builder.path, "path",
                                        "do you forget call path() to set path?"
                                )
                        )
                for ((key, value) in builder.queryMap) {
                    uriBuilder.appendQueryParameter(key, value)
                }
                result = uriBuilder.build()
            } else {
                result = Uri.parse(builder.url)
                if (builder.queryMap.isNotEmpty()) {
                    val uriBuilder = result.buildUpon()
                    for ((key, value) in builder.queryMap) {
                        uriBuilder.appendQueryParameter(key, value)
                    }
                    result = uriBuilder.build()
                }
            }
            return result
        }

        /**
         * 构建一个URL,如果构建失败会抛出异常
         */
        fun buildURL(): String {
            return buildURI().toString()
        }
    }

}