package com.xiaojinzi.component

import android.app.Activity
import com.xiaojinzi.component.support.Utils
import java.util.*

/**
 * Component 的 Activity 栈
 *
 * @author xiaojinzi
 */
object ComponentActivityStack {
    /**
     * the stack will be save all reference of Activity
     */
    private val activityStack: Stack<Activity> = Stack()

    /**
     * 进入栈
     */
    @Synchronized
    fun pushActivity(activity: Activity) {
        if (activityStack.contains(activity)) {
            return
        }
        activityStack.add(activity)
    }

    /**
     * remove the reference of Activity
     *
     * @author xiaojinzi
     */
    @Synchronized
    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    /**
     * @return whether the the size of stack of Activity is zero or not
     */
    @Synchronized
    fun isEmpty(): Boolean {
        return activityStack.isEmpty()
    }

    /**
     * 返回顶层的 Activity
     */
    @Synchronized
    fun getTopActivity(): Activity? {
        return if (isEmpty()) null else activityStack[activityStack.lastIndex]// 如果已经销毁, 就下一个
    }

    /**
     * 返回顶层的活着的 Activity
     */
    @Synchronized
    fun getTopAliveActivity(): Activity? {
        var result: Activity? = null
        if (!isEmpty()) {
            val size = activityStack.size
            for (i in size - 1 downTo 0) {
                val activity = activityStack[i]
                // 如果已经销毁, 就下一个
                if (!Utils.isActivityDestoryed(activity)) {
                    result = activity
                    break
                }
            }
        }
        return result
    }

    /**
     * 返回顶层的 Activity除了某一个
     */
    @Synchronized
    fun getTopActivityExcept(clazz: Class<out Activity?>): Activity? {
        val size = activityStack.size
        for (i in size - 1 downTo 0) {
            val itemActivity = activityStack[i]
            if (itemActivity.javaClass != clazz) {
                return itemActivity
            }
        }
        return null
    }

    /**
     * 返回顶层的第二个 Activity
     */
    @Synchronized
    fun getSecondTopActivity(): Activity? {
        return if (isEmpty() || activityStack.size < 2) null else activityStack[activityStack.lastIndex - 1]
    }

    /**
     * 返回底层的 Activity
     */
    @Synchronized
    fun getBottomActivity(): Activity? {
        return if (isEmpty() || activityStack.size < 1) null else activityStack[0]
    }

    /**
     * 是否存在某一个 Activity
     */
    @Synchronized
    fun isExistActivity(clazz: Class<out Activity>): Boolean {
        for (activity in activityStack) {
            if (activity.javaClass == clazz) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun isExistOtherActivityExcept(clazz: Class<out Activity>): Boolean {
        for (activity in activityStack) {
            if (activity.javaClass != clazz) {
                return true
            }
        }
        return false
    }

}