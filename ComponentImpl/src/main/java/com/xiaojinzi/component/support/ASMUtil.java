package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.application.IComponentHostApplication;

/**
 * 下面的方法都是空方法, 如果用户用了 Gradele 插件去配置各个模块的名称, 那么下面的空方法都会被 Gradle 插件
 * 利用 ASM 去填写对应的代码. 让代码生效
 *
 * @author xiaojinzi
 */
public class ASMUtil {

    /**
     * 此方法特别重要, 为了加快启动初始化的速度, 作者写了一个 Gradle 插件, 去动态修改此方法的实现.
     * 增加对 host 参数的判断, 并且返回对应模块的实现类
     * 最终修改后的代码会像下方注释后的代码一样
     *
     * @param host 对应模块的名称
     * @return 返回对应模块的 Application {@link IComponentHostApplication}
     */
    @Nullable
    public static IComponentHostApplication findModuleApplicationAsmImpl(@NonNull String host) {
        //if ("user".equals(host)) {
        //    return UserModuleApplicationGenerated();
        //}
        return null;
    }

}
