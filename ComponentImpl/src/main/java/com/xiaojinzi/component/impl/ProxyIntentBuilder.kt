package com.xiaojinzi.component.impl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.xiaojinzi.component.Component.getApplication
import com.xiaojinzi.component.support.ProxyIntentAct
import com.xiaojinzi.component.support.Utils

/**
 * 用于构建一个 {@link Intent}, 详情请看 {@link Navigator#proxyBundle(Bundle)}
 */
class ProxyIntentBuilder(
        private val bundleBuilder: IBundleBuilder<ProxyIntentBuilder> = IBundleBuilderImpl(),
        private val uriBuilder: IURIBuilder<ProxyIntentBuilder> = IURIBuilderImpl(),
) :
        IBundleBuilder<ProxyIntentBuilder> by bundleBuilder,
        IURIBuilder<ProxyIntentBuilder> by uriBuilder {

    private val mDelegateImplCallable: () -> ProxyIntentBuilder = {
        this
    }

    override var delegateImplCallable: () -> ProxyIntentBuilder
        get() = mDelegateImplCallable
        set(value) {
            bundleBuilder.delegateImplCallable = value
            uriBuilder.delegateImplCallable = value
        }

    init {
        delegateImplCallable = mDelegateImplCallable
    }

    private var options: Bundle? = null

    /**
     * Intent 的 flag,允许修改的
     */
    private var intentFlags: MutableList<Int> = ArrayList(2)

    /**
     * Intent 的 类别,允许修改的
     */
    private var intentCategories: MutableList<String> = ArrayList(2)

    private var proxyActivity: Class<out Activity> = ProxyIntentAct::class.java

    fun proxyActivity(clazz: Class<out Activity?>): ProxyIntentBuilder {
        Utils.checkNullPointer(clazz, "clazz")
        proxyActivity = clazz
        return this
    }

    fun addIntentFlags(vararg flags: Int): ProxyIntentBuilder {
        intentFlags.addAll(flags.toTypedArray())
        return this
    }

    fun addIntentCategories(vararg categories: String): ProxyIntentBuilder {
        intentCategories.addAll(categories.toList())
        return this
    }

    /**
     * 用于 API >= 16 的时候,调用 [Activity.startActivity]
     */
    fun options(options: Bundle?): ProxyIntentBuilder {
        this.options = options
        return this
    }

    /**
     * 构建一个代理的 [Intent].
     * 跳转的目标:
     * 1. 当你没有指定的时候, 默认是 [ProxyIntentAct], 此界面会自动处理, 并帮助您跳转到目标界面. 全程无感知
     * 2. 当你指定了一个 [Activity] 的时候, 则此 [Intent] 跳转目标为你自定义的 [Activity]
     * 你可以使用 [Router.with] 方法获取一个 [Navigator], 然后使用 [Navigator.proxyBundle]
     */
    fun buildProxyIntent(): Intent {
        val intent = Intent(getApplication(), proxyActivity)
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT, true)
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_URL, buildURL())
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_BUNDLE, bundle)
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_OPTIONS, options)
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_FLAGS, ArrayList(intentFlags))
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_CATEGORIES, ArrayList(intentCategories))
        return intent
    }

}