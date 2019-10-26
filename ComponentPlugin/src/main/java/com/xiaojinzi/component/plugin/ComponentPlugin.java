package com.xiaojinzi.component.plugin;

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * 组件化的 Gradle 插件, 为了生成一部分的代码, 代替反射查找这个过程, 整个流程已经设计好了
 * 只要把 ASMUtil 工具类中的空方法填写一下就可以了
 */
public class ComponentPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("=============ComponentPlugin===================================" + project.getProperties().get("android").getClass().getName());
        BaseAppModuleExtension appModuleExtension =  (BaseAppModuleExtension)project.getProperties().get("android");
        appModuleExtension.registerTransform(new ComponentTransform());
        System.out.println("====================== After ComponentPlugin");
        /*AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        BaseAppModuleExtension
        appExtension.registerTransform(new ComponentTransform());*/
    }

}
