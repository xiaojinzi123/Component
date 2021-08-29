package com.xiaojinzi.component.impl

import android.app.Activity
import android.content.Intent
import androidx.annotation.AnyThread
import androidx.annotation.CheckResult
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.support.NavigationDisposable

/**
 * 这个对象表示一个可调用的路由跳转
 */
@CheckClassNameAnno
interface Call {
    /**
     * 普通跳转
     */
    @AnyThread
    fun forward()

    /**
     * 普通跳转
     *
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigate(): NavigationDisposable

    /**
     * 普通跳转
     *
     * @param callback 当跳转完毕或者发生错误会回调
     */
    @AnyThread
    fun forward(callback: Callback?)

    /**
     * 执行跳转的具体逻辑
     * 返回值不可以为空, 是为了使用的时候更加的顺溜,不用判断空
     *
     * @param callback 当跳转完毕或者发生错误会回调,
     * 回调给用户的 [Callback], 回调中的各个方法, 每个方法至多有一个方法被调用, 并且只有一次
     * @return 返回的对象有可能是一个空实现对象 [Router.emptyNavigationDisposable],可以取消路由或者获取原始request对象
     */
    @AnyThread
    @CheckResult
    fun navigate(callback: Callback?): NavigationDisposable

    /**
     * 跳转拿到 [ActivityResult] 数据
     *
     * @param callback 当 [ActivityResult] 拿到之后或者发生错误会回调
     */
    @AnyThread
    fun forwardForResult(callback: BiCallback<ActivityResult>)

    /**
     * 跳转拿到 [ActivityResult] 数据
     *
     * @param callback 当 [ActivityResult] 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigateForResult(callback: BiCallback<ActivityResult>): NavigationDisposable

    /**
     * 跳转拿到 [Intent] 数据
     *
     * @param callback 当 [ActivityResult] 拿到之后或者发生错误会回调
     */
    @AnyThread
    fun forwardForIntent(callback: BiCallback<Intent>)

    /**
     * 跳转拿到 [Intent] 数据
     *
     * @param callback 当 [ActivityResult] 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigateForIntent(callback: BiCallback<Intent>): NavigationDisposable

    /**
     * 跳转拿到 [Intent] 数据
     *
     * @param callback           当 [ActivityResult] 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     */
    @AnyThread
    fun forwardForIntentAndResultCodeMatch(
        callback: BiCallback<Intent>,
        expectedResultCode: Int = Activity.RESULT_OK
    )

    /**
     * 跳转拿到 [Intent] 数据
     *
     * @param callback           当 [ActivityResult] 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigateForIntentAndResultCodeMatch(
        callback: BiCallback<Intent>,
        expectedResultCode: Int = Activity.RESULT_OK
    ): NavigationDisposable

    /**
     * 跳转为了匹配 [ActivityResult] 中的 [ActivityResult.resultCode]
     *
     * @param callback           当 [ActivityResult] 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     */
    @AnyThread
    fun forwardForResultCodeMatch(
        callback: Callback,
        expectedResultCode: Int = Activity.RESULT_OK
    )

    /**
     * 跳转为了匹配 [ActivityResult] 中的 [ActivityResult.resultCode]
     *
     * @param callback           当 [ActivityResult] 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigateForResultCodeMatch(
        callback: Callback,
        expectedResultCode: Int = Activity.RESULT_OK
    ): NavigationDisposable

    /**
     * 跳转拿到 [ActivityResult.resultCode] 数据
     *
     * @param callback 当 [ActivityResult] 拿到之后或者发生错误会回调
     */
    @AnyThread
    fun forwardForResultCode(callback: BiCallback<Int>)

    /**
     * 跳转拿到 [ActivityResult.resultCode] 数据
     *
     * @param callback 当 [ActivityResult] 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigateForResultCode(callback: BiCallback<Int>): NavigationDisposable

    /**
     * 跳转, 获取目标的 Intent 的. 不会真正的发起跳转
     * @return 可用于取消本次路由
     */
    @AnyThread
    fun forwardForTargetIntent(callback: BiCallback<Intent>)

    /**
     * 警告：切勿用作通知或者小部件的 Intent 使用
     * 此方法虽然在回调中返回了目标界面的真实 Intent, 但是是经过了整个路由的过程获取到的.
     * 甚至目标 Intent 都可能因为参数的不同而不同.
     * 所以通知或者小部件, 请使用如下方式, 如下方式不会发起路由. 只会返回一个代理的 Intent 供你使用.
     * 当你通过代理 Intent 发起跳转之后, 会发起真实的路由过程
     * ```
     * val targetProxyIntent = Router
     *       .newProxyIntentBuilder()
     *       .putString("name", "testName")
     *       // .....
     *       .buildProxyIntent();
     * ```
     * 跳转, 获取目标的 Intent 的. 不会真正的发起跳转
     * @return 可用于取消本次路由
     */
    @AnyThread
    @CheckResult
    fun navigateForTargetIntent(callback: BiCallback<Intent>): NavigationDisposable

}