package com.xiaojinzi.component.impl

import android.content.Intent
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.support.Consumer1
import java.util.*

/**
 * 为了完成跳转拿 [ActivityResult] 的功能, 需要预埋一个 [Fragment]
 * 此 [Fragment] 是不可见的, 它的主要作用：
 * 1. 用作跳转的
 * 2. 用作接受 [Fragment.onActivityResult] 的回调的值
 *
 *
 * 当发出多个路由的之后, 有可能多个 [RouterRequest] 对象中的 [RouterRequest.requestCode]
 * 是一致的, 那么就会造成 [ActivityResult] 回调的时候出现不对应的问题.
 * 这个问题在 [com.xiaojinzi.component.impl.NavigatorImpl.Help.isExist] 方法中
 * 得以保证
 *
 *
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 */
class RouterFragment : Fragment() {

    private val singleEmitterMap: MutableMap<RouterRequest, Consumer1<ActivityResult>> = HashMap()

    override fun onDestroy() {
        super.onDestroy()
        singleEmitterMap.clear()
    }

    /**
     * 此方法我觉得官方不应该废弃的. 因为新的方式其实在易用性上并不好.
     * 不过官方废弃了那我也只能支持官方的方式：[Call.navigateForTargetIntent]
     * 或者 [Call.forwardForTargetIntent] 都可以在回调中拿到目标界面的 Intent.
     * 这时候, 路由框架是不会发起跳转的.
     *
     * @param requestCode 业务请求码
     * @param resultCode  返回码
     * @param data        返回的数据
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 根据 requestCode 获取发射器
        var findConsumer: Consumer1<ActivityResult>? = null
        var findRequest: RouterRequest? = null
        // 找出 requestCode 一样的那个
        val keySet: Set<RouterRequest> = singleEmitterMap.keys
        for (request in keySet) {
            if (request.requestCode != null && request.requestCode == requestCode) {
                findRequest = request
                break
            }
        }
        if (findRequest != null) {
            findConsumer = singleEmitterMap[findRequest]
        }
        if (findConsumer != null) {
            try {
                findConsumer.accept(ActivityResult(requestCode, resultCode, data))
            } catch (ignore: Exception) {
                // ignore
            }
        }
        if (findRequest != null) {
            singleEmitterMap.remove(findRequest)
        }
    }

    fun addActivityResultConsumer(request: RouterRequest,
                                  consumer: Consumer1<ActivityResult>) {
        // 检测是否重复的在这个方法调用之前被检查掉了
        singleEmitterMap[request] = consumer
    }

    fun removeActivityResultConsumer(request: RouterRequest) {
        singleEmitterMap.remove(request)
    }

}