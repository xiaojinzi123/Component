package com.xiaojinzi.component;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.support.ObjectToJsonConverter;
import com.xiaojinzi.component.support.Utils;

/**
 * 这是组件化的一个配置类
 *
 * @see Component#init(boolean, Config)
 */
public class Config {

    @NonNull
    private Application application;
    @NonNull
    private String defaultScheme;

    private boolean isErrorCheck;
    private boolean isInitRouterAsync;
    private boolean isOptimizeInit;
    private boolean isAutoRegisterModule;
    private boolean isTipWhenUseApplication;
    private boolean isUseRouteRepeatCheckInterceptor = true;
    private long routeRepeatCheckDuration = 1000;
    private ObjectToJsonConverter objectToJsonConverter;
    private long notifyModuleChangedDelayTime;

    private Config(@NonNull Builder builder) {
        this.application = builder.application;
        this.isErrorCheck = builder.isErrorCheck;
        this.isInitRouterAsync = builder.isInitRouterAsync;
        this.isOptimizeInit = builder.isOptimizeInit;
        this.isAutoRegisterModule = builder.isAutoRegisterModule;
        this.isTipWhenUseApplication = builder.isTipWhenUseApplication;
        this.defaultScheme = builder.defaultScheme;
        this.isUseRouteRepeatCheckInterceptor = builder.isUseRouteRepeatCheckInterceptor;
        this.routeRepeatCheckDuration = builder.routeRepeatCheckDuration;
        this.objectToJsonConverter = builder.objectToJsonConverter;
        this.notifyModuleChangedDelayTime = builder.notifyModuleChangedDelayTime;
    }

    @NonNull
    public Application getApplication() {
        return application;
    }

    @NonNull
    public String getDefaultScheme() {
        return defaultScheme;
    }

    public boolean isErrorCheck() {
        return isErrorCheck;
    }

    public boolean isInitRouterAsync() {
        return isInitRouterAsync;
    }

    public boolean isOptimizeInit() {
        return isOptimizeInit;
    }

    public boolean isAutoRegisterModule() {
        return isAutoRegisterModule;
    }

    public boolean isTipWhenUseApplication() {
        return isTipWhenUseApplication;
    }

    public boolean isUseRouteRepeatCheckInterceptor() {
        return isUseRouteRepeatCheckInterceptor;
    }

    public long getRouteRepeatCheckDuration() {
        return routeRepeatCheckDuration;
    }

    @Nullable
    public ObjectToJsonConverter getObjectToJsonConverter() {
        return objectToJsonConverter;
    }

    public long getNotifyModuleChangedDelayTime() {
        return notifyModuleChangedDelayTime;
    }

    @NonNull
    public static Builder with(@NonNull Application application) {
        return new Builder(application);
    }

    public static class Builder {

        private Application application;
        private String defaultScheme = "router";
        // 是否进行检查, 默认是打开的, 仅在 debug 的时候有效
        private boolean isErrorCheck = true;
        private boolean isInitRouterAsync = false;
        private boolean isOptimizeInit = false;
        private boolean isAutoRegisterModule = false;
        private boolean isTipWhenUseApplication = true;
        private boolean isUseRouteRepeatCheckInterceptor = true;
        private long routeRepeatCheckDuration = 1000;
        private ObjectToJsonConverter objectToJsonConverter;
        private long notifyModuleChangedDelayTime = 0L;

        /*标记是否已经使用*/
        private boolean isUsed = false;

        public Builder(@NonNull Application application) {
            Utils.checkNullPointer(application, "application");
            this.application = application;
        }

        public Builder defaultScheme(String defaultScheme) {
            Utils.checkStringNullPointer(defaultScheme, "defaultScheme");
            this.defaultScheme = defaultScheme;
            return this;
        }

        public Builder initRouterAsync(boolean isInitRouterAsync) {
            this.isInitRouterAsync = isInitRouterAsync;
            return this;
        }

        public Builder errorCheck(boolean isCheck) {
            this.isErrorCheck = isCheck;
            return this;
        }

        public Builder optimizeInit(boolean isOptimizeInit) {
            this.isOptimizeInit = isOptimizeInit;
            return this;
        }

        public Builder autoRegisterModule(boolean isAutoRegisterModule) {
            this.isAutoRegisterModule = isAutoRegisterModule;
            return this;
        }

        /**
         * 设置是否在跳转的时候使用 {@link Application} 的时候日志提醒
         */
        public Builder tipWhenUseApplication(boolean isTipWhenUseApplication) {
            this.isTipWhenUseApplication = isTipWhenUseApplication;
            return this;
        }

        /**
         * 设置是否使用内置的路由跳转的重复检查的拦截器
         * 在一定时间内, Router 跳转如果发现 host 和 path 是一样的, 则认定为是一致的.
         * 那么第二次将会被拦截. 时间您可以通过 {@link #routeRepeatCheckDuration(long)} 设置
         */
        public Builder useRouteRepeatCheckInterceptor(boolean isUseRouteRepeatCheckInterceptor) {
            this.isUseRouteRepeatCheckInterceptor = isUseRouteRepeatCheckInterceptor;
            return this;
        }

        public Builder routeRepeatCheckDuration(long routeRepeatCheckDuration) {
            this.routeRepeatCheckDuration = routeRepeatCheckDuration;
            return this;
        }

        public Builder objectToJsonConverter(ObjectToJsonConverter objectToJsonConverter) {
            this.objectToJsonConverter = objectToJsonConverter;
            return this;
        }

        public Builder notifyModuleChangedDelayTime(long notifyModuleChangedDelayTime) {
            this.notifyModuleChangedDelayTime = notifyModuleChangedDelayTime;
            return this;
        }

        @NonNull
        public Config build() {
            // 参数检查
            Utils.checkNullPointer(this.application, "application");
            Utils.checkNullPointer(this.defaultScheme, "application");
            if (this.isAutoRegisterModule) {
                if (!this.isOptimizeInit) {
                    throw new UnsupportedOperationException("you must call optimizeInit(true) method");
                }
            }
            if (isUsed) {
                throw new UnsupportedOperationException("this builder only can build once!");
            }
            isUsed = true;
            // 提前创建对象
            Config config = new Config(this);
            // 解除占用
            this.application = null;
            this.defaultScheme = null;
            return config;
        }

    }

}
