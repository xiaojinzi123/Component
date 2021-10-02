package com.xiaojinzi.component.impl

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.AnyThread
import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.ComponentConstants
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.anno.support.NeedOptimizeAnno
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.error.ignore.ActivityResultException
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException
import com.xiaojinzi.component.error.ignore.NavigationFailException
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter
import com.xiaojinzi.component.impl.interceptor.OpenOnceInterceptor
import com.xiaojinzi.component.support.*
import com.xiaojinzi.component.support.NavigationDisposable.ProxyNavigationDisposableImpl
import java.util.*
import kotlin.collections.ArrayList

interface INavigator<T : INavigator<T>> : IRouterRequestBuilder<T>, Call {

    fun interceptors(vararg interceptorArr: RouterInterceptor): T
    fun interceptors(vararg interceptorClassArr: Class<out RouterInterceptor>): T
    fun interceptorNames(vararg interceptorNameArr: String): T
    fun requestCodeRandom(): T
    fun autoCancel(autoCancel: Boolean): T
    fun useRouteRepeatCheck(useRouteRepeatCheck: Boolean): T
    fun proxyBundle(bundle: Bundle): T

}

/**
 * 拦截器多个连接着走的执行器,源代码来源于 OkHTTP
 * 这个原理就是, 本身是一个 执行器 (Chain),当你调用 proceed 方法的时候,会创建下一个拦截器的执行对象
 * 然后调用当前拦截器的 intercept 方法
 * @param mInterceptors 拦截器列表,所有要执行的拦截器列表
 * @param mIndex        拦截器的下标
 * @param mRequest      第一次这个对象是不需要的
 * @param mCallback     这个是拦截器的回调,这个用户不能自定义,一直都是一个对象
 */
open class InterceptorChain(
        private val mInterceptors: List<RouterInterceptor?>,
        private val mIndex: Int,
        /**
         * 每一个拦截器执行器 [RouterInterceptor.Chain]
         * 都会有上一个拦截器给的 request 对象或者初始化的一个 request,用于在下一个拦截器
         * 中获取到 request 对象,并且支持拦截器自定义修改 request 对象或者直接创建一个新的传给下一个拦截器执行器
         */
        private val mRequest: RouterRequest,
        private val mCallback: RouterInterceptor.Callback
) : RouterInterceptor.Chain {

    /**
     * 调用的次数,如果超过1次就做相应的错误处理
     */
    private var calls = 0

    /**
     * 拦截器是否是否已经走完
     */
    @Synchronized
    protected fun isCompletedProcess(): Boolean {
        return mIndex >= mInterceptors.size
    }

    protected fun index(): Int {
        return mIndex
    }

    protected fun interceptors(): List<RouterInterceptor?> {
        return mInterceptors
    }

    protected fun rawCallback(): RouterInterceptor.Callback {
        return mCallback
    }

    override fun request(): RouterRequest {
        // 第一个拦截器的
        return mRequest
    }

    override fun callback(): RouterInterceptor.Callback {
        return rawCallback()
    }

    override fun proceed(request: RouterRequest) {
        proceed(request, callback())
    }

    private fun proceed(request: RouterRequest, callback: RouterInterceptor.Callback) {
        // ui 线程上执行
        Utils.postActionToMainThreadAnyway(Runnable {
            try {
                // 如果已经结束, 对不起就不执行了
                if (callback().isEnd()) {
                    return@Runnable
                }
                ++calls
                when {
                    isCompletedProcess() -> {
                        callback().onError(
                                NavigationFailException(
                                        IndexOutOfBoundsException(
                                                "size = " + mInterceptors.size + ",index = " + mIndex
                                        )
                                )
                        )
                    }
                    calls > 1 -> { // 调用了两次
                        callback().onError(
                                NavigationFailException(
                                        "interceptor " + mInterceptors[mIndex - 1]
                                                + " must call proceed() exactly once"
                                )
                        )
                    }
                    else -> {
                        // current Interceptor
                        val interceptor = mInterceptors[mIndex]
                        // 当拦截器最后一个的时候,就不是这个类了,是 DoActivityStartInterceptor 了
                        val next = InterceptorChain(
                                mInterceptors, mIndex + 1,
                                request, callback
                        )
                        // 提前同步 Query 到 Bundle
                        next.request().syncUriToBundle()
                        // 用户自定义的部分,必须在主线程
                        interceptor!!.intercept(next)
                    }
                }
            } catch (e: Exception) {
                callback().onError(e)
            }
        })
    }

}

/**
 * 这个类一部分功能应该是 [Router] 的构建者对象的功能,但是这里面更多的为导航的功能
 * 写了很多代码,所以名字就不叫 Builder 了
 */
open class NavigatorImpl<T : INavigator<T>>
@JvmOverloads constructor(
        context: Context? = null,
        fragment: Fragment? = null,
        private val routerRequestBuilder: IRouterRequestBuilder<T> = RouterRequestBuilderImpl(
                context = context,
                fragment = fragment,
        ),
) : IRouterRequestBuilder<T> by routerRequestBuilder,
        INavigator<T>, Call {

    /**
     * 自定义的拦截器列表,为了保证顺序才用一个集合的
     * 1. RouterInterceptor 类型
     * 2. Class<RouterInterceptor> 类型
     * 3. String 类型
     * 其他类型会 debug 的时候报错
     */
    private var customInterceptors: MutableList<Any>? = null

    /**
     * 标记这个 builder 是否已经被使用了,使用过了就不能使用了
     */
    private var isFinish = false

    /**
     * 是否自动取消
     */
    private var autoCancel = true

    /**
     * 是否检查路由是否重复, 默认是全局配置的开关
     */
    private var useRouteRepeatCheck = Component.requiredConfig().isUseRouteRepeatCheckInterceptor

    private fun getRealDelegateImpl(): T {
        return delegateImplCallable.invoke()
    }

    /**
     * 懒加载自定义拦截器列表
     */
    private fun lazyInitCustomInterceptors(size: Int) {
        if (customInterceptors == null) {
            customInterceptors = ArrayList(if (size > 3) size else 3)
        }
    }

    override fun interceptors(vararg interceptorArr: RouterInterceptor): T {
        Utils.debugCheckNullPointer(interceptorArr, "interceptorArr")
        lazyInitCustomInterceptors(interceptorArr.size)
        customInterceptors!!.addAll(interceptorArr.toList())
        return getRealDelegateImpl()
    }

    override fun interceptors(vararg interceptorClassArr: Class<out RouterInterceptor>): T {
        Utils.debugCheckNullPointer(interceptorClassArr, "interceptorClassArr")
        lazyInitCustomInterceptors(interceptorClassArr.size)
        customInterceptors!!.addAll(interceptorClassArr.toList())
        return getRealDelegateImpl()
    }

    override fun interceptorNames(vararg interceptorNameArr: String): T {
        Utils.debugCheckNullPointer(interceptorNameArr, "interceptorNameArr")
        lazyInitCustomInterceptors(interceptorNameArr.size)
        customInterceptors!!.addAll(interceptorNameArr.toList())
        return getRealDelegateImpl()
    }

    /**
     * requestCode 会随机的生成
     */
    override fun requestCodeRandom(): T {
        requestCode(requestCode = RANDOM_REQUEST_CODE)
        return getRealDelegateImpl()
    }

    override fun autoCancel(autoCancel: Boolean): T {
        this.autoCancel = autoCancel
        return getRealDelegateImpl()
    }

    override fun useRouteRepeatCheck(useRouteRepeatCheck: Boolean): T {
        this.useRouteRepeatCheck = useRouteRepeatCheck
        return getRealDelegateImpl()
    }

    /**
     * 当您使用 [ProxyIntentBuilder] 构建了一个 [Intent] 之后.
     * 此 [Intent] 的跳转目标是一个代理的界面. 具体是
     * [ProxyIntentAct] 或者是用户你自己自定义的 [<]
     * 携带的参数是是真正的目标的信息. 比如：
     * [ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_URL] 表示目标的 url
     * [ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_BUNDLE] 表示跳转到真正的目标的 [Bundle] 数据
     * ......
     * 当你自定义了代理界面, 那你可以使用[Router.with] 或者  [Router.with] 或者
     * [Router.with] 得到一个 [Navigator]
     * 然后你就可以使用[Navigator.proxyBundle] 直接导入跳转到真正目标所需的各种参数, 然后
     * 直接发起跳转, 通过条用 [Navigator.forward] 等方法
     * 示例代码：
     * <pre class="prettyprint">
     * public class XXXProxyActivity extends Activity {
     *      ...
     *      protected void onCreate(Bundle savedInstanceState) {
     *           super.onCreate(savedInstanceState);
     *           Router.with(this)
     *                   .proxyBundle(getIntent().getExtras())
     *                   .forward();
     *      }
     *      ...
     * }</pre>
     *
     * @see ProxyIntentAct
     */
    override fun proxyBundle(bundle: Bundle): T {
        Utils.checkNullPointer(bundle, "bundle")
        val reqUrl = bundle.getString(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_URL)
        val reqBundle = bundle.getBundle(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_BUNDLE)
        val reqOptions: Bundle? = bundle.getBundle(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_OPTIONS)
        val reqFlags: ArrayList<Int> = bundle
                .getIntegerArrayList(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_FLAGS) ?: ArrayList()
        val reqCategories =
                bundle.getStringArrayList(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_CATEGORIES)
        routerRequestBuilder.url(reqUrl!!)
        routerRequestBuilder.putAll(reqBundle!!)
        routerRequestBuilder.options(reqOptions)
        routerRequestBuilder.addIntentFlags(*reqFlags.toIntArray())
        routerRequestBuilder.addIntentCategories(*reqCategories!!.toTypedArray())
        return getRealDelegateImpl()
    }

    override fun build(): RouterRequest {
        var routerRequest = routerRequestBuilder.build()
        // 如果是随机的 requestCode, 则生成
        routerRequest = Help.randomlyGenerateRequestCode(routerRequest)
        // 现在可以检测 requestCode 是否重复
        val isExist = Help.isExist(routerRequest)
        if (isExist) { // 如果存在直接返回错误给 callback
            throw NavigationFailException(
                    "request&result code is " +
                            routerRequest.requestCode + " is exist!"
            )
        }
        return routerRequest
    }

    /**
     * 使用默认的 [android.app.Application] 作为
     * [Context]. 使用默认的 [android.app.Application]
     * 会添加 [Intent.FLAG_ACTIVITY_NEW_TASK] 标记
     */
    private fun useDefaultContext() {
        // 如果 Context 和 Fragment 都是空的,使用默认的 Application
        if (context == null && fragment == null) {
            context(context = Component.getApplication())
            // 配套加上 New_Task 的标志, 当用户自己传的 Application 需要自己添加这个 flag
            // 起到更好的提示用户是使用 Application 跳的
            addIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * 检查 forResult 的时候的各个参数是否合格
     */
    @Throws(Exception::class)
    private fun onCheckForResult() {
        if (context == null && fragment == null) {
            throw NavigationFailException(
                    NullPointerException(
                            "Context or Fragment is necessary if you want get ActivityResult"
                    )
            )
        }
        // 如果是使用 Context 的,那么就必须是 FragmentActivity,需要操作 Fragment
        // 这里的 context != null 判断条件不能去掉,不然使用 Fragment 跳转的就过不去了
        if (context != null && Utils.getActivityFromContext(context) !is FragmentActivity) {
            throw NavigationFailException(
                    IllegalArgumentException(
                            "context must be FragmentActivity or fragment must not be null " +
                                    "when you want get ActivityResult from target Activity"
                    )
            )
        }
        if (requestCode == null) {
            throw NavigationFailException(
                    NullPointerException(
                            "requestCode must not be null when you want get ActivityResult from target Activity, " +
                                    "if you use code, do you forget call requestCodeRandom() or requestCode(Integer). " +
                                    "if you use routerApi, do you forget mark method or parameter with @RequestCodeAnno() Annotation"
                    )
            )
        }
    }

    /**
     * 为了拿到 [ActivityResult.resultCode]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forwardForResultCode(@UiThread callback: BiCallback<Int>) {
        navigateForResultCode(callback)
    }

    /**
     * 为了拿到 [ActivityResult.resultCode]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @CheckResult
    override fun navigateForResultCode(@UiThread callback: BiCallback<Int>): NavigationDisposable {
        return navigateForResult(object : BiCallback.Map<ActivityResult, Int>(callback) {
            @Throws(Exception::class)
            override fun apply(activityResult: ActivityResult): Int {
                return activityResult.resultCode
            }
        })
    }

    @AnyThread
    @SuppressLint("CheckResult")
    override fun forwardForTargetIntent(callback: BiCallback<Intent>) {
        navigateForTargetIntent(callback)
    }

    @AnyThread
    @CheckResult
    override fun navigateForTargetIntent(callback: BiCallback<Intent>): NavigationDisposable {
        return realNavigateForTargetIntent(callback)
    }

    /**
     * 为了拿到 [ActivityResult.resultCode]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forwardForResultCodeMatch(
            @UiThread callback: Callback, expectedResultCode: Int
    ) {
        navigateForResultCodeMatch(callback, expectedResultCode)
    }

    /**
     * 为了拿到 [ActivityResult.resultCode]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @CheckResult
    override fun navigateForResultCodeMatch(
            @UiThread callback: Callback, expectedResultCode: Int
    ): NavigationDisposable {
        return navigateForResult(object : BiCallback<ActivityResult> {
            override fun onSuccess(result: RouterResult, activityResult: ActivityResult) {
                if (expectedResultCode == activityResult.resultCode) {
                    callback.onSuccess(result)
                } else {
                    callback.onError(
                            RouterErrorResult(
                                    result.originalRequest,
                                    ActivityResultException("the resultCode is not matching $expectedResultCode")
                            )
                    )
                }
            }

            override fun onError(errorResult: RouterErrorResult) {
                callback.onError(errorResult)
            }

            override fun onCancel(originalRequest: RouterRequest?) {
                callback.onCancel(originalRequest)
            }
        })
    }

    /**
     * 为了拿到 [Intent]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forwardForIntentAndResultCodeMatch(
            @UiThread callback: BiCallback<Intent>,
            expectedResultCode: Int
    ) {
        navigateForIntentAndResultCodeMatch(callback, expectedResultCode)
    }

    /**
     * 为了拿到 [Intent]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @CheckResult
    override fun navigateForIntentAndResultCodeMatch(
            @UiThread callback: BiCallback<Intent>,
            expectedResultCode: Int
    ): NavigationDisposable {
        return navigateForResult(object : BiCallback.Map<ActivityResult, Intent>(callback) {
            @Throws(Exception::class)
            override fun apply(activityResult: ActivityResult): Intent {
                return activityResult.intentWithResultCodeCheckAndGet(expectedResultCode)
            }
        })
    }

    /**
     * 为了拿到 [Intent]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forwardForIntent(@UiThread callback: BiCallback<Intent>) {
        navigateForIntent(callback)
    }

    /**
     * 为了拿到 [Intent]
     *
     * @param callback 回调方法
     */
    @AnyThread
    @CheckResult
    override fun navigateForIntent(@UiThread callback: BiCallback<Intent>): NavigationDisposable {
        return navigateForResult(object : BiCallback.Map<ActivityResult, Intent>(callback) {
            @Throws(Exception::class)
            override fun apply(activityResult: ActivityResult): Intent {
                return activityResult.intentCheckAndGet()
            }
        })
    }

    /**
     * 为了拿 [ActivityResult]
     *
     * @param callback 这里是为了拿返回的东西是不可以为空的
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forwardForResult(@UiThread callback: BiCallback<ActivityResult>) {
        navigateForResult(callback)
    }

    /**
     * 为了拿 [ActivityResult]
     *
     * @param callback 这里是为了拿返回的东西是不可以为空的
     */
    @AnyThread
    @CheckResult
    override fun navigateForResult(
            @UiThread callback: BiCallback<ActivityResult>
    ): NavigationDisposable {
        Utils.checkNullPointer(callback, "callback")
        return realNavigateForResult(callback)
    }

    /**
     * 没有返回值
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forward() {
        navigate(null)
    }

    /**
     * @return 返回的对象有可能是一个空实现对象 [Router.emptyNavigationDisposable]
     */
    @AnyThread
    @CheckResult
    override fun navigate(): NavigationDisposable {
        return navigate(null)
    }

    /**
     * 没有返回值
     *
     * @param callback 路由的回调
     */
    @AnyThread
    @SuppressLint("CheckResult")
    override fun forward(@UiThread callback: Callback?) {
        navigate(callback)
    }

    @AnyThread
    @CheckResult
    @Synchronized
    override fun navigate(@UiThread callback: Callback?): NavigationDisposable {
        // 构建请求对象
        var originalRequest: RouterRequest? = null
        // 可取消对象
        var interceptorCallback: InterceptorCallback? = null
        try {
            // 如果用户没填写 Context 或者 Fragment 默认使用 Application
            useDefaultContext()
            // 路由前的检查
            if (isFinish) {
                // 一个 Builder 不能被使用多次
                throw NavigationFailException("Builder can't be used multiple times")
            }
            if (context == null && fragment == null) {
                // 检查上下文和fragment
                throw NullPointerException("the parameter 'context' or 'fragment' both are null")
            }
            // 标记这个 builder 已经不能使用了
            isFinish = true
            // 生成路由请求对象
            originalRequest = build()
            // 创建整个拦截器到最终跳转需要使用的 Callback
            interceptorCallback = InterceptorCallback(originalRequest, callback)
            // Fragment 的销毁的自动取消
            if (autoCancel && originalRequest.fragment != null) {
                Router.mNavigationDisposableList.add(interceptorCallback)
            }
            // Activity 的自动取消
            if (autoCancel && Utils.getActivityFromContext(originalRequest.context) != null) {
                Router.mNavigationDisposableList.add(interceptorCallback)
            }
            val finalOriginalRequest: RouterRequest = originalRequest
            val finalInterceptorCallback: InterceptorCallback = interceptorCallback
            Utils.postActionToMainThread { // 真正的去执行路由
                realNavigate(finalOriginalRequest, customInterceptors, finalInterceptorCallback)
            }
            // 返回对象
            return interceptorCallback
        } catch (e: Exception) { // 发生路由错误
            if (interceptorCallback == null) {
                val errorResult = RouterErrorResult(originalRequest, e)
                RouterUtil.errorCallback(callback, null, errorResult)
            } else {
                // 这里错误回调也会让 interceptorCallback 内部的 isEnd 是 true, 所以不用去特意取消
                // 也会让整个路由终止
                interceptorCallback.onError(e)
            }
        }
        return Router.emptyNavigationDisposable
    }

    @AnyThread
    @CheckResult
    private fun realNavigateForResult(callback: BiCallback<ActivityResult>): NavigationDisposable {
        Utils.checkNullPointer(callback, "callback")
        val proxyDisposable = ProxyNavigationDisposableImpl()
        // 主线程执行
        Utils.postActionToMainThread(Runnable { // 这里这个情况属于没开始就被取消了
            if (proxyDisposable.isCanceled) {
                RouterUtil.cancelCallback(null, callback)
                return@Runnable
            }
            val realDisposable = doNavigateForResult(callback)
            proxyDisposable.setProxy(realDisposable)
        })
        return proxyDisposable
    }

    @AnyThread
    @CheckResult
    private fun realNavigateForTargetIntent(callback: BiCallback<Intent>): NavigationDisposable {
        Utils.checkNullPointer(callback, "callback")
        val proxyDisposable = ProxyNavigationDisposableImpl()
        // 主线程执行
        Utils.postActionToMainThread(Runnable { // 这里这个情况属于没开始就被取消了
            if (proxyDisposable.isCanceled) {
                RouterUtil.cancelCallback(null, callback)
                return@Runnable
            }
            val realDisposable = doNavigateForTargetIntent(callback)
            proxyDisposable.setProxy(realDisposable)
        })
        return proxyDisposable
    }

    /**
     * 必须在主线程中调用,就这里可能会出现一种特殊的情况：
     * 用户收到的回调可能是 error,但是全局的监听可能是 cancel,其实这个问题也能解决,
     * 就是路由调用之前提前通过方法 [Navigator.build] 提前构建一个 [RouterRequest] 出来判断
     * 但是没有那个必要去做这件事情了,等到有必要的时候再说,基本不会出现并且出现了也不是什么问题
     */
    @UiThread
    @CheckResult
    private fun doNavigateForResult(biCallback: BiCallback<ActivityResult>): NavigationDisposable {
        // 直接 gg
        Utils.checkNullPointer(biCallback, "biCallback")
        // 标记此次是需要框架帮助获取 ActivityResult 的
        isForResult(isForResult = true)
        // 做一个包裹实现至多只能调用一次内部的其中一个方法
        val biCallbackWrap: BiCallback<ActivityResult> = BiCallbackWrap(biCallback)
        // disposable 对象
        var finalNavigationDisposable: NavigationDisposable? = null
        return try {
            // 为了拿数据做的检查
            onCheckForResult()
            // 声明fragment
            val fm: FragmentManager = if (context == null) {
                fragment!!.childFragmentManager
            } else {
                (Utils.getActivityFromContext(context) as FragmentActivity?)!!.supportFragmentManager
            }
            // 寻找是否添加过 Fragment
            var findRxFragment =
                    fm.findFragmentByTag(ComponentConstants.ACTIVITY_RESULT_FRAGMENT_TAG) as RouterFragment?
            if (findRxFragment == null) {
                findRxFragment = RouterFragment()
                fm.beginTransaction()
                        .add(
                                findRxFragment,
                                ComponentConstants.ACTIVITY_RESULT_FRAGMENT_TAG
                        ) // 这里必须使用 now 的形式, 否则连续的话立马就会new出来. 因为判断进来了
                        .commitNowAllowingStateLoss()
            }
            val rxFragment: RouterFragment = findRxFragment
            // 导航方法执行完毕之后,内部的数据就会清空,所以之前必须缓存
            // 导航拿到 NavigationDisposable 对象
            // 可能是一个 空实现
            finalNavigationDisposable = navigate(object : CallbackAdapter() {
                @UiThread
                override fun onSuccess(routerResult: RouterResult) {
                    super.onSuccess(routerResult)
                    // 设置ActivityResult回调的发射器,回调中一个路由拿数据的流程算是完毕了

                    rxFragment.addActivityResultConsumer(
                            routerResult.originalRequest,
                            object : Consumer1<ActivityResult> {
                                override fun accept(result: ActivityResult) {
                                    Help.removeRequestCode(routerResult.originalRequest)
                                    biCallbackWrap.onSuccess(routerResult, result)
                                }
                            }
                    )

                }

                @UiThread
                override fun onError(errorResult: RouterErrorResult) {
                    super.onError(errorResult)
                    Help.removeRequestCode(errorResult.originalRequest)
                    // 这里为啥没有调用
                    biCallbackWrap.onError(errorResult)
                }

                @UiThread
                override fun onCancel(originalRequest: RouterRequest?) {
                    super.onCancel(originalRequest)
                    originalRequest?.let {
                        rxFragment.removeActivityResultConsumer(it)
                    }
                    Help.removeRequestCode(originalRequest)
                    biCallbackWrap.onCancel(originalRequest)
                }
            })
            // 添加这个 requestCode 到 map, 重复的事情不用考虑了, 在 build RouterRequest 的时候已经处理了
            Help.addRequestCode(finalNavigationDisposable.originalRequest())
            finalNavigationDisposable
        } catch (e: Exception) {
            if (finalNavigationDisposable == null) {
                RouterUtil.errorCallback(null, biCallbackWrap, RouterErrorResult(e))
                // 就只会打印出一个错误信息: 路由失败信息
            } else {
                // 取消这个路由, 此时其实会输出两个信息
                // 第一个是打印出路由失败的信息
                // 第二个是路由被取消的信息
                // 因为上面路由发起了才能有 RouterRequest 对象, 然后这里检查到 requestCode 重复了
                // 回调给用户的是 requestCode 重复的错误, 但是上面发起的路由还是得取消的. 不然就跳过去了
                RouterUtil.errorCallback(
                        null, biCallbackWrap,
                        RouterErrorResult(finalNavigationDisposable.originalRequest(), e)
                )
                // 取消上面执行的路由
                finalNavigationDisposable.cancel()
            }
            finalNavigationDisposable = null
            Router.emptyNavigationDisposable
        }
    }

    @UiThread
    @CheckResult
    private fun doNavigateForTargetIntent(biCallback: BiCallback<Intent>): NavigationDisposable {
        // 直接 gg
        Utils.checkNullPointer(biCallback, "biCallback")
        // 标记此次是需要框架帮助获取目标 Intent 的
        isForTargetIntent(isForTargetIntent = true)
        // 做一个包裹实现至多只能调用一次内部的其中一个方法
        val biCallbackWrap: BiCallback<Intent> = BiCallbackWrap(biCallback)
        // disposable 对象
        var finalNavigationDisposable: NavigationDisposable? = null
        return try {
            // 导航方法执行完毕之后,内部的数据就会清空,所以之前必须缓存
            // 导航拿到 NavigationDisposable 对象
            // 可能是一个 空实现
            finalNavigationDisposable = navigate(object : CallbackAdapter() {
                @UiThread
                override fun onSuccess(routerResult: RouterResult) {
                    super.onSuccess(routerResult)
                    val targetIntent = routerResult.targetIntent
                    if (targetIntent == null) {
                        biCallbackWrap.onError(RouterErrorResult(routerResult.originalRequest, NavigationFailException("targetIntent is null")))
                    } else {
                        biCallbackWrap.onSuccess(routerResult, targetIntent)
                    }
                }

                @UiThread
                override fun onError(errorResult: RouterErrorResult) {
                    super.onError(errorResult)
                    // 这里为啥没有调用
                    biCallbackWrap.onError(errorResult)
                }

                @UiThread
                override fun onCancel(originalRequest: RouterRequest?) {
                    super.onCancel(originalRequest)
                    biCallbackWrap.onCancel(originalRequest)
                }
            })
            finalNavigationDisposable
        } catch (e: Exception) {
            // 没发起就不行了
            if (finalNavigationDisposable == null) {
                RouterUtil.errorCallback(null, biCallbackWrap, RouterErrorResult(e))
            } else {
                RouterUtil.errorCallback(
                        null, biCallbackWrap,
                        RouterErrorResult(finalNavigationDisposable.originalRequest(), e)
                )
                // 取消上面执行的路由
                finalNavigationDisposable.cancel()
            }
            finalNavigationDisposable = null
            Router.emptyNavigationDisposable
        }
    }

    /**
     * 真正的执行路由
     *
     * @param originalRequest           最原始的请求对象
     * @param customInterceptors        自定义的拦截器
     * @param routerInterceptorCallback 回调对象
     */
    @UiThread
    private fun realNavigate(
            originalRequest: RouterRequest,
            customInterceptors: List<Any>?,
            routerInterceptorCallback: RouterInterceptor.Callback
    ) {

        // 自定义拦截器,初始化拦截器的个数 8 个够用应该不会经常扩容
        val allInterceptors: MutableList<RouterInterceptor> = ArrayList(10)

        // 此拦截器用于执行一些整个流程开始之前的事情
        allInterceptors.add(object : RouterInterceptor {
            override fun intercept(chain: RouterInterceptor.Chain) {
                // 执行跳转前的 Callback
                RouterRequestHelp.executeBeforeAction(chain.request())
                // 继续下一个拦截器
                chain.proceed(chain.request())
            }
        })

        // 添加路由检查拦截器
        if (useRouteRepeatCheck) {
            allInterceptors.add(OpenOnceInterceptor)
        }
        // 添加共有拦截器
        allInterceptors.addAll(
                InterceptorCenter.globalInterceptorList
        )
        // 添加用户自定义的拦截器
        allInterceptors.addAll(
                getCustomInterceptors(originalRequest, customInterceptors)
        )
        // 负责加载目标 Intent 的页面拦截器的拦截器. 此拦截器后不可再添加其他拦截器
        allInterceptors.add(PageInterceptor(originalRequest, allInterceptors))

        // 创建执行器
        val chain: RouterInterceptor.Chain = InterceptorChain(
                allInterceptors, 0,
                originalRequest, routerInterceptorCallback
        )
        // 执行
        chain.proceed(originalRequest)
    }

    /**
     * 这个拦截器的 Callback 是所有拦截器执行过程中会使用的一个 Callback,
     * 这是唯一的一个, 每个拦截器对象拿到的此对象都是一样的
     * 内部的错误成功额方法可以调用 N 次
     */
    private class InterceptorCallback(
            /**
             * 最原始的请求,用户构建的,不会更改的
             */
            private val mOriginalRequest: RouterRequest,
            /**
             * 用户的回调
             */
            private val mCallback: Callback?
    ) : NavigationDisposable, RouterInterceptor.Callback {
        /**
         * 标记是否完成,出错或者成功都算是完成了,不能再继续调用了
         */
        private var _isComplete = false

        /**
         * 取消
         */
        private var _isCanceled = false


        override fun onSuccess(result: RouterResult) {
            Utils.checkNullPointer(result)
            synchronized(this) {
                if (isEnd()) {
                    return
                }
                _isComplete = true
                RouterUtil.successCallback(mCallback, result)
            }
        }

        override fun onError(error: Throwable) {
            Utils.checkNullPointer(error)
            synchronized(this) {
                if (isEnd()) {
                    return
                }
                _isComplete = true
                // 创建错误的对象
                val errorResult = RouterErrorResult(mOriginalRequest, error)
                // 回调执行
                RouterUtil.errorCallback(mCallback, null, errorResult)
            }
        }

        override fun isComplete(): Boolean {
            return synchronized(this) { return _isComplete }
        }

        override fun isCanceled(): Boolean {
            return synchronized(this) { return _isCanceled }
        }

        override fun isEnd(): Boolean {
            return synchronized(this) { isComplete() || isCanceled }
        }

        override fun originalRequest(): RouterRequest {
            return mOriginalRequest
        }

        @AnyThread
        override fun cancel() {
            synchronized(this) {
                if (isEnd()) {
                    return
                }
                // 标记取消成功
                _isCanceled = true
                RouterUtil.cancelCallback(mOriginalRequest, mCallback)
            }
        }


    }

    /**
     * 处理页面拦截器的. 因为页面拦截器可能会更改 [Uri]. 导致目标改变.
     * 那么新的页面拦截器也应该被加载执行.
     * 最后确认 [Uri] 的目标没被改变的时候
     * 就可以加载 [DoActivityStartInterceptor] 执行跳转了.
     */
    @UiThread
    private class PageInterceptor(
            private val mOriginalRequest: RouterRequest,
            private val mAllInterceptors: MutableList<RouterInterceptor>
    ) : RouterInterceptor {
        @Throws(Exception::class)
        override fun intercept(chain: RouterInterceptor.Chain) {
            val currentUri = chain.request().uri
            // 这个地址要执行的页面拦截器,这里取的时候一定要注意了,不能拿最原始的那个 request,因为上面的拦截器都能更改 request,
            // 导致最终跳转的界面和你拿到的页面拦截器不匹配,所以这里一定是拿上一个拦截器传给你的 request 对象
            val targetPageInterceptors = RouterCenter.listPageInterceptors(currentUri)
            mAllInterceptors.add(
                    PageInterceptorUriCheckInterceptor(
                            mOriginalRequest,
                            mAllInterceptors,
                            currentUri,
                            targetPageInterceptors,
                            0
                    )
            )
            // 执行下一个拦截器,正好是上面代码添加的拦截器
            chain.proceed(chain.request())
        }
    }

    /**
     * 处理页面拦截器的. 因为页面拦截器可能会更改 [Uri]. 导致目标改变.
     * 那么新的页面拦截器也应该被加载执行.
     * 最后确认 [Uri] 的目标没被改变的时候
     * 就可以加载 [DoActivityStartInterceptor] 执行跳转了.
     */
    @UiThread
    private class PageInterceptorUriCheckInterceptor(
            private val mOriginalRequest: RouterRequest,
            private val mAllInterceptors: MutableList<RouterInterceptor>,
            /**
             * 进入页面拦截器之前的 [Uri]
             */
            private val mBeforePageInterceptorUri: Uri?,
            private val mPageInterceptors: List<RouterInterceptor>?,
            private var mPageIndex: Int
    ) : RouterInterceptor {
        @Throws(Exception::class)
        override fun intercept(chain: RouterInterceptor.Chain) {
            if (mPageIndex < 0) {
                throw NavigationFailException(
                        IndexOutOfBoundsException(
                                "size = " + mPageInterceptors!!.size + ",index = " + mPageIndex
                        )
                )
            }
            val currentUri = chain.request().uri
            val isSameTarget = if (mBeforePageInterceptorUri != null) {
                RouterCenter.isSameTarget(mBeforePageInterceptorUri, currentUri)
            } else {
                false
            }

            // 如果目标是相同的, 说明页面拦截器并没有改变跳转的目标
            if (isSameTarget) {
                // 没有下一个了
                if (mPageInterceptors == null || mPageIndex >= mPageInterceptors.size) {
                    // 真正的执行跳转的拦截器, 如果正常跳转了 DoActivityStartInterceptor 拦截器就直接返回了
                    // 如果没有正常跳转过去, 内部会继续走拦截器, 会执行到后面的这个
                    mAllInterceptors.add(DoActivityStartInterceptor(mOriginalRequest))
                } else {
                    mAllInterceptors.add(mPageInterceptors[mPageIndex])
                    mAllInterceptors.add(
                            PageInterceptorUriCheckInterceptor(
                                    mOriginalRequest, mAllInterceptors, mBeforePageInterceptorUri,
                                    mPageInterceptors, ++mPageIndex
                            )
                    )
                }
            } else {
                mAllInterceptors.add(PageInterceptor(mOriginalRequest, mAllInterceptors))
            }
            // 执行下一个拦截器,正好是上面代码添加的拦截器
            chain.proceed(chain.request())
        }
    }

    /**
     * 这是拦截器的最后一个拦截器了
     * 实现拦截器列表中的最后一环, 内部去执行了跳转的代码
     * 1.如果跳转的时候没有发生异常, 说明可以跳转过去
     * 如果失败了进行降级处理
     */
    @UiThread
    private class DoActivityStartInterceptor(private val mOriginalRequest: RouterRequest) :
            RouterInterceptor {
        /**
         * @param chain 拦截器执行连接器
         * @throws Exception
         */
        @UiThread
        @Throws(Exception::class)
        override fun intercept(chain: RouterInterceptor.Chain) {
            // 这个 request 对象已经不是最原始的了,但是可能是最原始的,就看拦截器是否更改了这个对象了
            val finalRequest = chain.request()
            // 执行真正路由跳转回出现的异常
            var routeException: Exception? = null
            val targetIntent: Intent? = try {
                // 真正执行跳转的逻辑, 失败的话, 备用计划就会启动
                RouterCenter.openUri(finalRequest)
            } catch (e: Exception) { // 错误的话继续下一个拦截器
                routeException = e
                // 继续下一个拦截器
                chain.proceed(finalRequest)
                null
            }
            // 如果正常跳转成功需要执行下面的代码
            if (routeException == null) {
                // 成功的回调
                chain.callback()
                        .onSuccess(RouterResult(mOriginalRequest, finalRequest, targetIntent))
            } else {
                try {
                    // 获取路由的降级处理类
                    val routerDegrade = getRouterDegrade(finalRequest)
                            ?: // 抛出异常走 try catch 的逻辑
                            throw NavigationFailException("degrade route fail, it's url is " + mOriginalRequest.uri.toString())
                    // 降级跳转
                    val targetIntent: Intent? = RouterCenter.routerDegrade(
                            finalRequest,
                            routerDegrade.onDegrade(finalRequest)
                    )
                    // 成功的回调
                    chain.callback()
                            .onSuccess(RouterResult(mOriginalRequest, finalRequest, targetIntent))
                } catch (ignore: Exception) {
                    // 如果版本足够就添加到异常堆中, 否则忽略降级路由的错误
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        routeException.addSuppressed(ignore)
                    }
                    throw routeException
                }
            }
        }

        /**
         * 获取降级的处理类
         *
         * @param finalRequest 最终的路由请求
         */
        private fun getRouterDegrade(finalRequest: RouterRequest): RouterDegrade? {
            // 获取所有降级类
            val routerDegradeList = RouterDegradeCenter.getInstance()
                    .globalRouterDegradeList
            var result: RouterDegrade? = null
            for (i in routerDegradeList.indices) {
                val routerDegrade = routerDegradeList[i]
                // 如果匹配
                val isMatch = routerDegrade.isMatch(finalRequest)
                if (isMatch) {
                    result = routerDegrade
                    break
                }
            }
            return result
        }
    }

    /**
     * 一些帮助方法
     */
    private object Help {
        /**
         * 和[RouterFragment] 配套使用
         */
        private val mRequestCodeSet: MutableSet<String> = HashSet()
        private val r = Random()

        /**
         * 如果 requestCode 是 [Navigator.RANDOM_REQUEST_CODE].
         * 则随机生成一个 requestCode
         *
         * @return [1, 256]
         */
        @NeedOptimizeAnno
        fun randomlyGenerateRequestCode(request: RouterRequest): RouterRequest {
            // 如果不是想要随机生成,就直接返回
            if (RANDOM_REQUEST_CODE != request.requestCode) {
                return request
            }
            // 转化为构建对象
            val requestBuilder = request.toBuilder()
            var generateRequestCode = r.nextInt(256) + 1
            // 如果生成的这个 requestCode 存在,就重新生成
            while (
                    isExist(
                            Utils.getActivityFromContext(requestBuilder.context),
                            requestBuilder.fragment,
                            generateRequestCode
                    )
            ) {
                generateRequestCode = r.nextInt(256) + 1
            }
            return requestBuilder
                    .apply {
                        this.requestCode(requestCode = generateRequestCode)
                    }
                    .build()
        }

        /**
         * 检测同一个 Fragment 或者 Activity 发起的多个路由 request 中的 requestCode 是否存在了
         *
         * @param request 路由请求对象
         */
        fun isExist(request: RouterRequest): Boolean {
            if (request.requestCode == null) {
                return false
            }
            // 这个 Context 关联的 Activity,用requestCode 去拿数据的情况下
            // Context 必须是一个 Activity 或者 内部的 baseContext 是 Activity
            val act = Utils.getActivityFromContext(request.context)
            // 这个requestCode不会为空, 用这个方法的地方是必须填写 requestCode 的
            return isExist(act, request.fragment, request.requestCode)
        }

        /**
         * 这里分别检测 [Activity]、[Fragment] 和 requestCode 的重复.
         * 即使一个路由使用了 [Activity] + 123, 另一个用 [Fragment] + 123 也没问题是因为
         * 这两个分别会被预埋一个 [RouterFragment].
         * 所以他们共享一个[RouterFragment] 接受 [ActivityResult] 的
         */
        fun isExist(
                act: Activity?, fragment: Fragment?,
                requestCode: Int
        ): Boolean {
            if (act != null) {
                return mRequestCodeSet.contains(act.javaClass.name + requestCode)
            } else if (fragment != null) {
                return mRequestCodeSet.contains(fragment.javaClass.name + requestCode)
            }
            return false
        }

        /**
         * 添加一个路由请求的 requestCode
         *
         * @param request 路由请求对象
         */
        fun addRequestCode(request: RouterRequest?) {
            if (request?.requestCode == null) {
                return
            }
            val requestCode = request.requestCode
            // 这个 Context 关联的 Activity,用requestCode 去拿数据的情况下
            // Context 必须是一个 Activity 或者 内部的 baseContext 是 Activity
            val act = Utils.getActivityFromContext(request.context)
            if (act != null) {
                mRequestCodeSet.add(act.javaClass.name + requestCode)
            } else if (request.fragment != null) {
                mRequestCodeSet.add(request.fragment.javaClass.name + requestCode)
            }
        }

        /**
         * 移除一个路由请求的 requestCode
         *
         * @param request 路由请求对象
         */
        fun removeRequestCode(request: RouterRequest?) {
            if (request?.requestCode == null) {
                return
            }
            val requestCode = request.requestCode
            // 这个 Context 关联的 Activity,用requestCode 去拿数据的情况下
            // Context 必须是一个 Activity 或者 内部的 baseContext 是 Activity
            val act = Utils.getActivityFromContext(request.context)
            if (act != null) {
                mRequestCodeSet.remove(act.javaClass.name + requestCode)
            } else if (request.fragment != null) {
                mRequestCodeSet.remove(request.fragment.javaClass.name + requestCode)
            }
        }
    }

    companion object {
        /**
         * requestCode 如果等于这个值,就表示是随机生成的
         * 从 1-256 中随机生成一个,如果生成的正好是目前正在用的,会重新生成一个
         */
        const val RANDOM_REQUEST_CODE = Int.MIN_VALUE

        /**
         * 返回自定义的拦截器
         */
        @UiThread
        @Suppress("UNCHECKED_CAST")
        @Throws(InterceptorNotFoundException::class)
        private fun getCustomInterceptors(
                originalRequest: RouterRequest,
                customInterceptors: List<Any>?
        ): List<RouterInterceptor> {
            if (customInterceptors.isNullOrEmpty()) {
                return emptyList()
            }
            val result: MutableList<RouterInterceptor> = ArrayList(customInterceptors.size)
            for (customInterceptor in customInterceptors) {
                if (customInterceptor is RouterInterceptor) {
                    result.add(customInterceptor)
                } else if (customInterceptor is Class<*>) {
                    val interceptor =
                            RouterInterceptorCache.getInterceptorByClass((customInterceptor as Class<out RouterInterceptor>))
                    if (interceptor == null) {
                        throw InterceptorNotFoundException("can't find the interceptor and it's className is " + customInterceptor as Class<*> + ",target url is " + originalRequest.uri.toString())
                    } else {
                        result.add(interceptor)
                    }
                } else if (customInterceptor is String) {
                    val interceptor = InterceptorCenter.getByName(customInterceptor)
                    if (interceptor == null) {
                        throw InterceptorNotFoundException("can't find the interceptor and it's name is " + customInterceptor + ",target url is " + originalRequest.uri.toString())
                    } else {
                        result.add(interceptor)
                    }
                }
            }
            return result
        }
    }

}

@CheckClassNameAnno
class Navigator(
        context: Context? = null,
        fragment: Fragment? = null,
        private val navigator: INavigator<Navigator> = NavigatorImpl(context = context, fragment = fragment)
) : INavigator<Navigator> by navigator {

    init {
        navigator.delegateImplCallable = {
            this
        }
    }

}