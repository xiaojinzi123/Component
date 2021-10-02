package com.xiaojinzi.component.impl

import android.content.Context
import android.content.Intent
import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.error.UnknowException
import com.xiaojinzi.component.error.ignore.ActivityResultException
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException
import com.xiaojinzi.component.error.ignore.NavigationFailException
import com.xiaojinzi.component.error.ignore.TargetActivityNotFoundException
import com.xiaojinzi.component.support.CallbackAdapter
import com.xiaojinzi.component.support.NavigationDisposable
import com.xiaojinzi.component.support.Utils
import io.reactivex.*
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function


/**
 * 使用这个可以结合 RxJava 中的
 * {@link Single} 和 {@link Completable} 使用,会很方便
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
object RxRouter {

    @JvmStatic
    fun with(fragmentFlag: String): RxFragmentNavigator {
        return RxFragmentNavigator(fragmentFlag = fragmentFlag)
    }

    @JvmStatic
    fun with(): RxNavigator {
        return RxNavigator()
    }

    /**
     * 这个方法父类也有一个静态的,但是父类返回的是 [Navigator] 而这个返回的是
     *
     * @return [RxNavigator]
     */
    @JvmStatic
    fun with(context: Context): RxNavigator {
        return RxNavigator(context = context)
    }

    /**
     * 这个方法父类也有一个静态的,但是父类返回的是 [Navigator] 而这个返回的是
     * [RxNavigator]
     */
    @JvmStatic
    fun with(fragment: Fragment): RxNavigator {
        return RxNavigator(fragment = fragment)
    }

    class RxNavigator(
            context: Context? = null,
            fragment: Fragment? = null,
            private val navigator: INavigator<RxNavigator> = NavigatorImpl(context = context, fragment = fragment)
    ) : INavigator<RxNavigator> by navigator // 占位
    {

        /**
         * 一个可以拿到 ActivityResult 的路由 Observable
         */
        @CheckResult
        fun activityResultCall(): Single<ActivityResult> {
            return Single.create(SingleOnSubscribe { emitter ->
                if (emitter.isDisposed) {
                    return@SingleOnSubscribe
                }
                val navigationDisposable: NavigationDisposable = navigateForResult(object : BiCallback.BiCallbackAdapter<ActivityResult>() {
                    override fun onSuccess(result: RouterResult, activityResult: ActivityResult) {
                        super.onSuccess(result, activityResult)
                        if (emitter.isDisposed) {
                            return
                        }
                        emitter.onSuccess(activityResult)
                    }

                    override fun onError(errorResult: RouterErrorResult) {
                        super.onError(errorResult)
                        if (emitter.isDisposed) {
                            return
                        }
                        RxHelp.onErrorSolve(emitter, errorResult.error)
                    }

                    override fun onCancel(originalRequest: RouterRequest?) {
                        super.onCancel(originalRequest)
                    }

                })
                emitter.setCancellable { navigationDisposable.cancel() }
            })
        }

        /**
         * 一个可以拿到 Intent 的 Observable
         *
         * @see .activityResultCall
         */
        @CheckResult
        fun intentCall(): Single<Intent> {
            return activityResultCall()
                    .map { activityResult ->
                        activityResult.intentCheckAndGet()
                    }
        }

        /**
         * 拿到 resultCode 的 Observable
         */
        @CheckResult
        fun resultCodeCall(): Single<Int> {
            return activityResultCall()
                    .map { activityResult ->
                        activityResult.resultCode
                    }
        }

        /**
         * requestCode 一定是相同的,resultCode 如果匹配了就剩下 Intent 参数了
         * 这个方法不会给你 Intent 对象,只会给你是否 resultCode 匹配成功了
         * 那么
         *
         * @param expectedResultCode 期望的 resultCode 的值
         * @return 返回一个完成状态的 Observable
         * @see .activityResultCall
         */
        @CheckResult
        fun resultCodeMatchCall(expectedResultCode: Int): Completable {
            return activityResultCall()
                    .doOnSuccess {
                        if (it.resultCode != expectedResultCode) {
                            throw ActivityResultException("the resultCode is not matching $expectedResultCode")
                        }
                    }
                    .ignoreElement()
        }

        /**
         * 这个方法不仅可以匹配 resultCode,还可以拿到 Intent,当不匹配或者 Intent 为空的时候都会报错哦
         *
         * @param expectedResultCode 期望的 resultCode 的值
         * @return 返回一个发射 Single 的 Observable
         * @see .activityResultCall
         */
        @CheckResult
        fun intentResultCodeMatchCall(expectedResultCode: Int): Single<Intent> {
            return activityResultCall()
                    .map { activityResult -> activityResult.intentWithResultCodeCheckAndGet(expectedResultCode) }
        }

        /**
         * 一个完成状态的 Observable 的路由跳转
         */
        @CheckResult
        fun call(): Completable {
            return Completable.create(CompletableOnSubscribe { emitter ->
                if (emitter.isDisposed) {
                    return@CompletableOnSubscribe
                }
                // 导航拿到 NavigationDisposable 对象
                // 可能是一个 空实现,这些个回调都是回调在主线程的
                val navigationDisposable = navigate(object : CallbackAdapter() {
                    @UiThread
                    override fun onSuccess(routerResult: RouterResult) {
                        super.onSuccess(routerResult)
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                    }

                    @UiThread
                    override fun onError(errorResult: RouterErrorResult) {
                        super.onError(errorResult)
                        RxHelp.onErrorSolve(emitter, errorResult.error)
                    }
                })
                // 设置取消
                emitter.setCancellable { navigationDisposable.cancel() }
            })
        }

        init {
            navigator.delegateImplCallable = {
                this
            }
        }

    }

    /**
     * 一些帮助方法
     */
    private object RxHelp {
        /**
         * 错误处理
         */
        fun onErrorSolve(emitter: SingleEmitter<out Any?>, e: Throwable) {
            when (e) {
                is InterceptorNotFoundException -> {
                    onErrorEmitter(emitter, e)
                }
                is NavigationFailException -> {
                    onErrorEmitter(emitter, e)
                }
                is TargetActivityNotFoundException -> {
                    onErrorEmitter(emitter, e)
                }
                is ActivityResultException -> {
                    onErrorEmitter(emitter, e)
                }
                else -> {
                    onErrorEmitter(emitter, UnknowException(e))
                }
            }
        }

        /**
         * 错误处理
         */
        fun onErrorSolve(emitter: CompletableEmitter, e: Throwable) {
            when (e) {
                is InterceptorNotFoundException -> {
                    onErrorEmitter(emitter, e)
                }
                is NavigationFailException -> {
                    onErrorEmitter(emitter, e)
                }
                is TargetActivityNotFoundException -> {
                    onErrorEmitter(emitter, e)
                }
                is ActivityResultException -> {
                    onErrorEmitter(emitter, e)
                }
                else -> {
                    onErrorEmitter(emitter, UnknowException(e))
                }
            }
        }

        /**
         * 发射错误,目前这些个发射错误都是为了 [RxRouter] 写的,发射的错误和正确的 item 被发射都应该
         * 最终发射在主线程
         */
        fun onErrorEmitter(@UiThread emitter: SingleEmitter<out Any>?,
                                   e: Throwable) {
            if (emitter == null || emitter.isDisposed) {
                return
            }
            if (Utils.isMainThread()) {
                emitter.onError(e)
            } else {
                Utils.postActionToMainThreadAnyway { emitter.onError(e) }
            }
        }

        /**
         * 发射错误,目前这些个发射错误都是为了 [RxRouter] 写的,发射的错误和正确的 item 被发射都应该
         * 最终发射在主线程
         *
         * @param emitter [Completable] 的发射器
         * @param e 跑出的异常
         */
        fun onErrorEmitter(@UiThread emitter: CompletableEmitter,
                                   e: Throwable) {
            if (emitter == null || emitter.isDisposed) {
                return
            }
            if (Utils.isMainThread()) {
                emitter.onError(e)
            } else {
                Utils.postActionToMainThreadAnyway { emitter.onError(e) }
            }
        }
    }

}