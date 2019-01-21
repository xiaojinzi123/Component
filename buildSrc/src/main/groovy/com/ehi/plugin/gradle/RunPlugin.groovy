package com.ehi.plugin.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.annotation.Nullable

/**
 * 这个插件用于让一个 Lib 模块可以直接运行的插件,源码来自得到组件化项目
 */
class RunPlugin implements Plugin<Project> {

    static MAIN_MODULE = "mainModuleName";
    static CAN_RUN_ALONE = "canRunAlone";
    static IS_LIBRARY_ACTUAL = "isLibraryActual";
    static DEBUG_COMPONENTS = "debugComponents";
    static RELEASE_COMPONENTS = "releaseComponents";

    // 默认是app，直接运行assembleRelease的时候，等同于运行app:assembleRelease
    String currentAssembleModuleName = null;

    // 当前运行的 module 名字
    String currentModuleName = null

    @Override
    void apply(Project project) {

        // 判断是否定义了主模块的名称
        if (!project.rootProject.hasProperty(MAIN_MODULE)) {
            throw new RuntimeException("you should set " + MAIN_MODULE + " in rootproject's gradle.properties")
        }

        // 要最先获取
        currentModuleName = project.path.replace(":", "")

        log("currentModuleName=" + currentModuleName)
        log("project.gradle.startParameter.taskNames=" + project.gradle.startParameter.taskNames)
        log("project.path=" + project.path)

        // 拿到当前编译的任务,可能为空,如果能拿到
        // 1.当运行 App,那么依赖的所有模块都会被执行这个插件,那么每一个插件都能获取到同一个 moduleName=app
        // 2.当运行 Lib 的时候, moduleName="LibName" 这个就是业务模块的名称了
        // may null
        AssembleTask assembleTask = getTaskInfo(project.gradle.startParameter.taskNames)
        if (assembleTask != null) {
            log("assembleTask.isDebug=" + assembleTask.isDebug)
            log("assembleTask.moduleName=" + assembleTask.moduleName)
        }

        // 获取当前编译模块的名称
        fetchCurrentAssembleModuleName(project, assembleTask)

        // 如果本身是一个库,那么默认是不可运行的
        String isRunAloneStr = project.properties.get(CAN_RUN_ALONE)

        // 是否实际上是一个 lib
        boolean isLibraryActual = Boolean.parseBoolean(project.properties.get(IS_LIBRARY_ACTUAL))
        boolean isRunAlone = false
        String mainModule = project.rootProject.property(MAIN_MODULE)
        log("mainModule = " + mainModule)
        boolean isMainModule = currentModuleName.equals(mainModule)
        log("isMainModule = " + isMainModule)
        if (isMainModule) {
            isRunAlone = true
        } else {
            isRunAlone = (isRunAloneStr == null || isRunAloneStr.trim().length() == 0) ? false : Boolean.parseBoolean(isRunAloneStr)
            log("isRunAlone1 = " + isRunAlone)
            if (isRunAlone && assembleTask != null) {
                //对于要编译的组件和主项目，isRunAlone修改为true，其他组件都强制修改为false
                //这就意味着组件不能引用主项目，这在层级结构里面也是这么规定的
                if (!currentModuleName.equals(currentAssembleModuleName)) {
                    //isRunAlone = false
                }
            }
        }

        log("isRunAlone2 = " + isRunAlone)

        // 根据配置添加各种组件依赖，并且自动化生成组件加载代码
        if (isRunAlone) {
            project.apply plugin: 'com.android.application'
            // 如果当前的模块的名字和当前编译的模块的名字一样
            if (!isMainModule) {
                project.android.sourceSets {
                    main {
                        manifest.srcFile 'src/main/runalone/AndroidManifest.xml'
                        java.srcDirs = ['src/main/java', 'src/main/runalone/java']
                        res.srcDirs = ['src/main/res', 'src/main/runalone/res']
                        assets.srcDirs = ['src/main/assets', 'src/main/runalone/assets']
                        jniLibs.srcDirs = ['src/main/jniLibs', 'src/main/runalone/jniLibs']
                    }
                }
            }
            if (assembleTask != null && currentModuleName.equals(currentAssembleModuleName)) {
                log("prepare to dependOn lib")
                compileComponents(assembleTask, project)
                //project.android.registerTransform(new ComCodeTransform(project))
            }
            log("apply plugin is com.android.application")
        } else {
            project.apply plugin: 'com.android.library'
            log("apply plugin is com.android.library")
        }


    }

    /**
     * 根据当前的task，获取要运行的组件，规则如下：
     * assembleRelease ---app
     * app:assembleRelease :app:assembleRelease ---app
     * sharecomponent:assembleRelease :sharecomponent:assembleRelease ---sharecomponent
     * @param assembleTask
     */
    private void fetchCurrentAssembleModuleName(Project project, @Nullable AssembleTask assembleTask) {
        if (assembleTask == null) {
            currentAssembleModuleName = project.rootProject.property(MAIN_MODULE)
        } else {
            if (assembleTask.moduleName == null || assembleTask.moduleName.trim().length() <= 0) {
                currentAssembleModuleName = project.rootProject.property(MAIN_MODULE)
            } else {
                currentAssembleModuleName = assembleTask.moduleName.trim()
            }
        }
        if (currentAssembleModuleName == null || currentAssembleModuleName.trim().length() <= 0) {
            throw new RuntimeException("you should set " + MAIN_MODULE + " in rootproject's gradle.properties,value can't be empty")
        }
        log("currentAssembleModuleName = " + currentAssembleModuleName)
    }

    @javax.annotation.Nullable
    private AssembleTask getTaskInfo(List<String> taskNames) {
        for (String task : taskNames) {
            if (task.toUpperCase().contains("ASSEMBLE")
                    || task.contains("aR")
                    || task.toUpperCase().contains("TINKER")
                    || task.toUpperCase().contains("INSTALL")
                    || task.toUpperCase().contains("RESGUARD")) {
                AssembleTask assembleTask = new AssembleTask()
                if (task.toUpperCase().contains("DEBUG")) {
                    assembleTask.isDebug = true
                }
                String[] strs = task.split(":")
                // :app:assembleDebug
                // 拿到 app 这个模块的名称
                assembleTask.moduleName = strs.length > 1 ? strs[strs.length - 2] : null
                return assembleTask
            }
        }
        return null
    }

    /**
     * 自动添加依赖，只在运行assemble任务的才会添加依赖，因此在开发期间组件之间是完全感知不到的，这是做到完全隔离的关键
     * 支持两种语法：module或者groupId:artifactId:version(@aar),前者之间引用module工程，后者使用maven中已经发布的aar
     * @param assembleTask
     * @param project
     */
    private void compileComponents(AssembleTask assembleTask, Project project) {

        String components

        if (assembleTask != null && assembleTask.isDebug) {
            components = (String) project.properties.get(DEBUG_COMPONENTS)
        } else {
            components = (String) project.properties.get(RELEASE_COMPONENTS)
        }

        if (components == null || components.length() == 0) {
            log("there is no add dependencies ")
            return
        }
        String[] compileComponents = components.split(",")
        if (compileComponents == null || compileComponents.length == 0) {
            log("there is no add dependencies ")
            return
        }

        for (String str : compileComponents) {
            log("comp is " + str)
            if (str.contains(":")) {
                /**
                 * 示例语法:groupId:artifactId:version(@aar)
                 * compileComponent=com.luojilab.reader:readercomponent:1.0.0
                 * 注意，前提是已经将组件aar文件发布到maven上，并配置了相应的repositories
                 */
                project.dependencies.add("compile", str)
                log("add dependencies lib  : " + str)
            } else {
                /**
                 * 示例语法:module
                 * compileComponent=readercomponent,sharecomponent
                 */
                project.dependencies.add("compile", project.project(':' + str))
                log("add dependencies project : " + str)
            }
        }

    }

    private class AssembleTask {
        boolean isDebug = false
        String moduleName;
    }

    private void log(String message) {
        println(currentModuleName + " ---> " + message)
    }

}