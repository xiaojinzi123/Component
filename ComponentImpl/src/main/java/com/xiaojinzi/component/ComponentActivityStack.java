package com.xiaojinzi.component;

import android.app.Activity;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Stack;

/**
 * Component 的 Activity 栈
 *
 * @author xiaojinzi
 */
public class ComponentActivityStack {

    /**
     * the stack will be save all reference of Activity
     */
    private Stack<Activity> activityStack = new Stack<>();

    private ComponentActivityStack() {
    }

    private static class Holder {
        private static ComponentActivityStack INSTANCE = new ComponentActivityStack();
    }

    @MainThread
    public static ComponentActivityStack getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 进入栈
     */
    public synchronized void pushActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (activityStack.contains(activity)) {
            return;
        }
        activityStack.add(activity);
    }

    /**
     * remove the reference of Activity
     *
     * @author xiaojinzi
     */
    public synchronized void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    /**
     * @return whether the the size of stack of Activity is zero or not
     */
    public synchronized boolean isEmpty() {
        if (activityStack == null || activityStack.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * @return the size of stack of Activity
     */
    public synchronized int getSize() {
        if (activityStack == null) {
            return 0;
        }
        return activityStack.size();
    }

    /**
     * 返回顶层的 Activity
     */
    @Nullable
    public synchronized Activity getTopActivity() {
        return isEmpty() || activityStack.size() < 1 ? null : activityStack.get(activityStack.size() - 1);
    }

    /**
     * 返回顶层的 Activity除了某一个
     */
    @Nullable
    public synchronized Activity getTopActivityExcept(@NonNull Class<? extends Activity> clazz) {
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity itemActivity = activityStack.get(i);
            if (itemActivity.getClass() != clazz) {
                return itemActivity;
            }
        }
        return null;
    }

    /**
     * 返回顶层的第二个 Activity
     */
    @Nullable
    public synchronized Activity getSecondTopActivity() {
        return isEmpty() || activityStack.size() < 2 ? null : activityStack.get(activityStack.size() - 2);
    }

    /**
     * 返回底层的 Activity
     */
    @Nullable
    public synchronized Activity getBottomActivity() {
        return isEmpty() || activityStack.size() < 1 ? null : activityStack.get(0);
    }

    /**
     * 是否存在某一个 Activity
     */
    public synchronized boolean isExistActivity(@NonNull Class<? extends Activity> clazz) {
        for (Activity activity : activityStack) {
            if (activity.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isExistOtherActivityExcept(@NonNull Class<? extends Activity> clazz) {
        for (Activity activity : activityStack) {
            if (activity.getClass() != clazz) {
                return true;
            }
        }
        return false;
    }

}
