package com.xiaojinzi.component.support

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.xiaojinzi.component.ComponentActivityStack.getTopActivityExcept
import com.xiaojinzi.component.impl.Router

/**
 * 此界面是一个无界面的, 当使用者通过 [ProxyIntentBuilder]
 * 构建一个代理 [android.content.Intent] 之后. 此 `Intent` 可以交给
 * 任何一个可以发起此 `Intent` 的地方, 比如：
 * 1. 系统小部件
 * 2. 消息栏
 * 3. 任何 PendingIntent 使用到的地方
 */
class ProxyIntentAct : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取数据
        val bundle = intent.extras
        var launchActivity = getTopActivityExcept(this.javaClass)
        val isUseSelfActivity = launchActivity == null
        if (launchActivity == null) {
            launchActivity = this
        }
        // 如果不是使用此 Activity 跳转, 那么立即销毁自己
        if (!isUseSelfActivity) {
            finish()
        }
        // 发起跳转
        Router.with(launchActivity)
                .proxyBundle(bundle!!)
                .afterEventAction {
                    if (isUseSelfActivity) {
                        finish()
                    }
                    Unit
                }
                .forward()
    }

    companion object {
        const val EXTRA_ROUTER_PROXY_INTENT = "router_proxy_intent"
        const val EXTRA_ROUTER_PROXY_INTENT_URL = "router_proxy_intent_url"
        const val EXTRA_ROUTER_PROXY_INTENT_BUNDLE = "router_proxy_intent_bundle"
        const val EXTRA_ROUTER_PROXY_INTENT_OPTIONS = "router_proxy_intent_options"
        const val EXTRA_ROUTER_PROXY_INTENT_FLAGS = "router_proxy_intent_flags"
        const val EXTRA_ROUTER_PROXY_INTENT_CATEGORIES = "router_proxy_intent_categories"
    }
}