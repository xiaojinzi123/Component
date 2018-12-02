package com.ehi.component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 组件化的配置类
 * time   : 2018/08/09
 *
 * @author : xiaojinzi 30212
 */
public class ComponentConfig {

    private static boolean isDebug = false;

    public static void init(boolean isDebug) {
        ComponentConfig.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

}
