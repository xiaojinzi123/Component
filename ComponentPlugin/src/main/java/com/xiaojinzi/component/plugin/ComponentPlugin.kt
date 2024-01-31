package com.xiaojinzi.component.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joom.grip.Grip
import com.joom.grip.GripFactory
import com.joom.grip.annotatedWith
import com.joom.grip.classes
import com.joom.grip.mirrors.getType
import com.xiaojinzi.component.plugin.bean.ActivityAttrDoc
import com.xiaojinzi.component.plugin.bean.ModuleDoc
import com.xiaojinzi.component.plugin.bean.ModuleJsonDoc
import com.xiaojinzi.component.plugin.util.IOUtil
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.objectweb.asm.Opcodes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ComponentPlugin : Plugin<Project> {

    companion object {
        const val TAG = "ComponentPlugin"
        const val ROUTER_FOLDER = "component_router_doc_folder"
        const val ROUTER_ENABLE = "component_router_doc_enable"
    }

    abstract class ModifyClassesTask : DefaultTask() {

        @get:InputFiles
        abstract val allJars: ListProperty<RegularFile>

        @get:InputFiles
        abstract val allDirectories: ListProperty<Directory>

        @get:OutputFile
        abstract val output: RegularFileProperty

        @get:Classpath
        abstract val bootClasspath: ListProperty<RegularFile>

        @get:CompileClasspath
        abstract var classpath: FileCollection

        private var isMergeOutputFileStr = project
            .properties["component_isMergeOutputFile"]
            ?.toString() ?: ""

        @TaskAction
        fun taskAction() {

            // 读取配置的属性 isMergeOutputFile
            val isMergeOutputFile = runCatching {
                isMergeOutputFileStr.toBoolean()
            }.getOrNull() ?: false

            // /Users/hhkj/Documents/code/android/github/Component/Demo/app2/build/intermediates/classes/debug/ALL/classes.jar
            val outputFile = output.asFile.get()
            val allJarList = allJars.get()

            println("${ComponentPlugin.TAG}, isMergeOutputFile = $isMergeOutputFile")
            println("${ComponentPlugin.TAG}, output = ${outputFile.path}, outputFileIsExist = ${outputFile.exists()}")

            // 输入的 jar、aar、源码
            val inputs = (allJarList + allDirectories.get()).map { it.asFile.toPath() }

            // 系统依赖
            val classPaths = bootClasspath.get().map { it.asFile.toPath() }
                .toSet() + classpath.files.map { it.toPath() }

            val grip: Grip =
                GripFactory.newInstance(Opcodes.ASM9)
                    .create(classPaths + inputs)

            val targetAllJars = if (isMergeOutputFile) {
                // 是否 AllJars 中有输出文件
                val isAllJarsContainsOutputFile = allJarList
                    .find {
                        it.asFile == outputFile
                    } != null
                if (isAllJarsContainsOutputFile) {
                    val tempFile = File.createTempFile(
                        "componentOutput",
                        ".${outputFile.extension}"
                    )
                    println("${ComponentPlugin.TAG}, tempFile = ${tempFile.path}")
                    outputFile.copyTo(
                        target = tempFile,
                        overwrite = true,
                    )
                    allJarList
                        .filter {
                            it.asFile != outputFile
                        } + listOf(
                        RegularFile { tempFile }
                    )
                } else {
                    allJarList
                }
            } else {
                allJarList
            }

            // 找到所有满足条件的 class
            val moduleNameMap = mutableMapOf<String, String>()
            grip
                .select(classes)
                .from(inputs)
                .where(
                    annotatedWith(
                        annotationType = getType(
                            descriptor = "Lcom/xiaojinzi/component/anno/support/ModuleApplicationGeneratedAnno;",
                        )
                    )
                )
                .execute()
                .classes
                .map { it.name }
                .forEach { name ->
                    val key = name
                        .removePrefix(prefix = "com.xiaojinzi.component.impl.application.")
                        .removeSuffix(suffix = "ModuleAppGenerated")
                        .removeSuffix(suffix = "ModuleAppGeneratedDefault")
                    if (name.endsWith(
                            suffix = "ModuleAppGeneratedDefault"
                        )
                    ) {
                        if (!moduleNameMap.containsKey(key = key)) {
                            moduleNameMap[key] = "$name.class"
                        }
                    } else {
                        moduleNameMap[key] = "$name.class"
                    }
                }

            // 找到所有满足条件的 class
            val interceptorMap = grip
                .select(classes)
                .from(inputs)
                .where(
                    annotatedWith(
                        annotationType = getType(
                            descriptor = "Lcom/xiaojinzi/component/anno/support/InterceptorGeneratedAnno;",
                        )
                    )
                )
                .execute()
                .classes
                .associate {
                    it.name
                        .removePrefix(prefix = "com.xiaojinzi.component.impl.interceptor.")
                        .removeSuffix(suffix = "InterceptorGenerated") to "${it.name}.class"
                }

            val routerMap = grip
                .select(classes)
                .from(inputs)
                .where(
                    annotatedWith(
                        annotationType = getType(
                            descriptor = "Lcom/xiaojinzi/component/anno/support/RouterGeneratedAnno;",
                        )
                    )
                )
                .execute()
                .classes
                .associate {
                    it.name
                        .removePrefix(prefix = "com.xiaojinzi.component.impl.")
                        .removeSuffix(suffix = "RouterGenerated") to "${it.name}.class"
                }

            val routerDegradeMap = grip
                .select(classes)
                .from(inputs)
                .where(
                    annotatedWith(
                        annotationType = getType(
                            descriptor = "Lcom/xiaojinzi/component/anno/support/RouterDeGradeGeneratedAnno;",
                        )
                    )
                )
                .execute()
                .classes
                .associate {
                    it.name
                        .removePrefix(prefix = "com.xiaojinzi.component.impl.")
                        .removeSuffix(suffix = "RouterDegradeGenerated") to "${it.name}.class"
                }

            val serviceMap = grip
                .select(classes)
                .from(inputs)
                .where(
                    annotatedWith(
                        annotationType = getType(
                            descriptor = "Lcom/xiaojinzi/component/anno/support/ServiceGeneratedAnno;",
                        )
                    )
                )
                .execute()
                .classes
                .associate {
                    it.name
                        .removePrefix(prefix = "com.xiaojinzi.component.impl.service.")
                        .removeSuffix(suffix = "ServiceGenerated") to "${it.name}.class"
                }

            val fragmentMap = grip
                .select(classes)
                .from(inputs)
                .where(
                    annotatedWith(
                        annotationType = getType(
                            descriptor = "Lcom/xiaojinzi/component/anno/support/FragmentGeneratedAnno;",
                        )
                    )
                )
                .execute()
                .classes
                .associate {
                    it.name
                        .removePrefix(prefix = "com.xiaojinzi.component.impl.fragment.")
                        .removeSuffix(suffix = "FragmentGenerated") to "${it.name}.class"
                }

            /*println("${ComponentPlugin1.TAG}, moduleNameMap = $moduleNameMap")
            println("${ComponentPlugin1.TAG}, interceptorMap = $interceptorMap")
            println("${ComponentPlugin1.TAG}, routerMap = $routerMap")
            println("${ComponentPlugin1.TAG}, routerDegradeMap = $routerDegradeMap")
            println("${ComponentPlugin1.TAG}, serviceMap = $serviceMap")
            println("${ComponentPlugin1.TAG}, fragmentMap = $fragmentMap")*/

            val jarOutput = JarOutputStream(
                BufferedOutputStream(
                    FileOutputStream(
                        output.get().asFile
                    )
                )
            )

            targetAllJars.forEach { file ->
                val jarFile = JarFile(file.asFile)
                jarFile.entries().iterator().forEach { jarEntry ->
                    if ("com/xiaojinzi/component/support/ASMUtil.class" == jarEntry.name) {
                        println("${ComponentPlugin.TAG}, 找到目标 ASMUtil.class")
                        val asmUtilClassBytes = ASMUtilClassUtil.getClassBytes(
                            moduleNameMap = moduleNameMap,
                            interceptorMap = interceptorMap,
                            routerMap = routerMap,
                            routerDegradeMap = routerDegradeMap,
                            serviceMap = serviceMap,
                            fragmentMap = fragmentMap,
                        )
                        /*runCatching {
                            File("./Temp/ASMUtil.class")
                                .outputStream()
                                .use { outputStream ->
                                    outputStream.write(
                                        asmUtilClassBytes
                                    )
                                }
                        }*/
                        jarOutput.putNextEntry(JarEntry(jarEntry.name))
                        jarOutput.write(asmUtilClassBytes)
                        jarOutput.closeEntry()
                    } else {
                        try {
                            jarOutput.putNextEntry(JarEntry(jarEntry.name))
                            jarFile.getInputStream(jarEntry).use {
                                it.copyTo(jarOutput)
                            }
                            jarOutput.closeEntry()
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }
                jarFile.close()
            }

            allDirectories.get().forEach { directory ->
                directory.asFile.walk().forEach { file ->
                    if (file.isFile) {
                        val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
                        jarOutput.putNextEntry(
                            JarEntry(
                                relativePath.replace(
                                    File.separatorChar,
                                    '/'
                                )
                            )
                        )
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(jarOutput)
                        }
                        jarOutput.closeEntry()
                    }
                }
            }

            jarOutput.close()

        }

    }

    override fun apply(project: Project) {

        val asmDisable = project.findProperty("component_asm_disable") as? Boolean ?: false

        if (!asmDisable) {
            with(project) {
                plugins.withType(AppPlugin::class.java) {

                    val androidComponents = extensions
                        .findByType(AndroidComponentsExtension::class.java)
                    androidComponents?.onVariants { variant ->
                        val name = "${variant.name}ModifyASMUtil"
                        val taskProvider = tasks.register<ModifyClassesTask>(name) {
                            group = "component"
                            description = name
                            bootClasspath.set(androidComponents.sdkComponents.bootClasspath)
                            classpath = variant.compileClasspath
                        }

                        variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                            .use(taskProvider)
                            .toTransform(
                                ScopedArtifact.CLASSES,
                                ModifyClassesTask::allJars,
                                ModifyClassesTask::allDirectories,
                                ModifyClassesTask::output
                            )

                    }

                }
            }
        }

        // 生成文档功能的
        routerDocTask(
            project = project
        )

        cleanTask(
            project = project
        )

    }

    @Throws(IOException::class)
    private fun generateRouterDoc(docFolder: File) {
        val routerJsonFolder = File(docFolder, "router")
        if (!routerJsonFolder.exists() || routerJsonFolder.isFile) {
            return
        }
        val files = routerJsonFolder.listFiles() ?: return
        val moduleDocs: MutableList<ModuleDoc> = ArrayList()
        val gson = Gson()
        for (file in files) {
            val fileName = file.name
            // 没一个匹配到的文件都是一个模块的路由信息
            if (file.exists() && file.isFile && fileName.endsWith(".json")) {
                val moduleDoc = ModuleDoc()
                val moduleName = fileName.substring(0, fileName.length - 5)
                // 读取文件中的 json
                val json = IOUtil.readFileAsString(file)
                // 读取到整个模块的 json
                val data = gson.fromJson<List<ModuleJsonDoc>>(
                    json,
                    object : TypeToken<ArrayList<ModuleJsonDoc?>?>() {}.type
                )
                // 循环路由信息, 如果是 Activity, 则看看在生成目录是否有对应的属性 json
                for (moduleJsonDoc in data) {
                    if (moduleJsonDoc.targetActivity != null && "" != moduleJsonDoc.targetActivity) {
                        val attrFile =
                            File(File(docFolder, "attr"), moduleJsonDoc.targetActivity + ".json")
                        if (attrFile.exists() && attrFile.isFile) {
                            val attrJson = IOUtil.readFileAsString(attrFile)
                            val activityAttrDocs = gson.fromJson<List<ActivityAttrDoc>>(
                                attrJson,
                                object : TypeToken<ArrayList<ActivityAttrDoc?>?>() {}.type
                            )
                            moduleJsonDoc.activityAttrDocs = activityAttrDocs
                        }
                    }
                }
                moduleDoc.moduleName = moduleName
                moduleDoc.data = data
                moduleDocs.add(moduleDoc)
            }
        }
        val json = gson.toJson(moduleDocs)
        // System.out.println("json = " + json);
        val htmlStr = (this
            .javaClass
            .getResourceAsStream("/routerDocIndex.html")
            ?.bufferedReader()
            ?.readText() ?: "")
            .replace(oldValue = "@{routerDoc}", newValue = json)
        val outputFolder = File(docFolder, "output")
        if (!outputFolder.exists() || outputFolder.isFile) {
            outputFolder.mkdirs()
        }
        File(outputFolder, "index.html")
            .outputStream()
            .use { outputS ->
                outputS.write(htmlStr.toByteArray())
            }
    }

    private fun routerDocTask(project: Project) {
        val task = project.task("routerDoc") {
            this.doLast {
                val routerEnable =
                    project.property(ComponentPlugin.ROUTER_ENABLE) as? Boolean ?: false
                println("${ComponentPlugin.TAG}, routerEnable = $routerEnable")
                if (!routerEnable) {
                    return@doLast
                }
                val folderStr = project.property(ComponentPlugin.ROUTER_FOLDER) as? String
                println("${ComponentPlugin.TAG}, folderStr = $folderStr")
                if (folderStr.isNullOrEmpty()) {
                    return@doLast
                }
                val folder = File(folderStr)
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                println("${ComponentPlugin.TAG}, folder = $folder")
                if (folder.isDirectory) {
                    try {
                        generateRouterDoc(
                            docFolder = folder,
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        task.group = "component"
    }

    private fun cleanTask(project: Project) {
        val task = project.task("routerDocClean") {
            this.doLast {
                if (project.hasProperty(ComponentPlugin.ROUTER_FOLDER)) {
                    val folderStr =
                        project.property(ComponentPlugin.ROUTER_FOLDER) as? String ?: ""
                    if (folderStr.isNotEmpty()) {
                        val folder = File(folderStr)
                        deleteFile(folder)
                    }
                }
            }
        }
        task.group = "component"
    }

    private fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.isFile) {
                file.delete()
            } else {
                val files = file.listFiles()
                if (files != null) {
                    for (subFile in files) {
                        deleteFile(subFile)
                    }
                }
            }
        }
    }

}