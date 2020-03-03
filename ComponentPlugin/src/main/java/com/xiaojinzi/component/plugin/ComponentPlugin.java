package com.xiaojinzi.component.plugin;

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaojinzi.component.plugin.bean.ActivityAttrDoc;
import com.xiaojinzi.component.plugin.bean.ModuleDoc;
import com.xiaojinzi.component.plugin.bean.ModuleJsonDoc;
import com.xiaojinzi.component.plugin.util.IOUtil;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;

/**
 * 组件化的 Gradle 插件, 为了生成一部分的代码, 代替反射查找这个过程, 整个流程已经设计好了
 * 只要把 ASMUtil 工具类中的空方法填写一下就可以了
 */
public class ComponentPlugin implements Plugin<Project> {

    public static final String ROUTER_FOLDER = "component_router_doc_folder";

    @Override
    public void apply(final Project project) {
        BaseAppModuleExtension appModuleExtension = (BaseAppModuleExtension) project.getProperties().get("android");
        Object asmUtilOutputPath = project.findProperty("component_asm_util_class_output_path");
        String asmUtilOutputPathStr = null;
        if (asmUtilOutputPath instanceof String) {
            asmUtilOutputPathStr = (String) asmUtilOutputPath;
        }
        appModuleExtension.registerTransform(new ModifyASMUtilTransform(asmUtilOutputPathStr));
        // 生成文档功能的
        routerDocTask(project);
        cleanTask(project);
    }

    private void routerDocTask(final Project project) {
        Task task = project.task("routerDoc", new Action<Task>() {
            @Override
            public void execute(Task task) {
                task.doLast(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        if (project.hasProperty(ROUTER_FOLDER)) {
                            Object folderObj = project.property(ROUTER_FOLDER);
                            if (folderObj instanceof String) {
                                String folderStr = (String) folderObj;
                                if (!folderStr.isEmpty()) {
                                    File folder = new File(folderStr);
                                    if (folder.exists() && folder.isDirectory()) {
                                        try {
                                            generateRouterDoc(folder);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
        task.setGroup("component");
    }

    private void cleanTask(final Project project) {
        Task task = project.task("routerDocClean", new Action<Task>() {
            @Override
            public void execute(Task task) {
                task.doLast(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        if (project.hasProperty(ROUTER_FOLDER)) {
                            Object folderObj = project.property(ROUTER_FOLDER);
                            if (folderObj instanceof String) {
                                String folderStr = (String) folderObj;
                                if (!folderStr.isEmpty()) {
                                    File folder = new File(folderStr);
                                    deleteFile(folder);
                                }
                            }
                        }
                    }
                });
            }
        });
        task.setGroup("component");
    }

    private void generateRouterDoc(File docFolder) throws IOException {
        File routerJsonFolder = new File(docFolder, "router");
        if (!routerJsonFolder.exists() || routerJsonFolder.isFile()) {
            return;
        }
        File[] files = routerJsonFolder.listFiles();
        if (files == null) {
            return;
        }
        List<ModuleDoc> moduleDocs = new ArrayList<>();
        Gson gson = new Gson();
        for (File file : files) {
            String fileName = file.getName();
            // 没一个匹配到的文件都是一个模块的路由信息
            if (file.exists() && file.isFile() && fileName.endsWith(".json")) {
                ModuleDoc moduleDoc = new ModuleDoc();
                String moduleName = fileName.substring(0, fileName.length() - 5);
                // 读取文件中的 json
                String json = IOUtil.readFileAsString(file);
                // 读取到整个模块的 json
                List<ModuleJsonDoc> data = gson.fromJson(json, new TypeToken<ArrayList<ModuleJsonDoc>>() {
                }.getType());
                // 循环路由信息, 如果是 Activity, 则看看在生成目录是否有对应的属性 json
                for (ModuleJsonDoc moduleJsonDoc : data) {
                    if (moduleJsonDoc.getTargetActivity() != null && !"".equals(moduleJsonDoc.getTargetActivity())) {
                        File attrFile = new File(new File(docFolder, "attr"), moduleJsonDoc.getTargetActivity() + ".json");
                        if (attrFile.exists() && attrFile.isFile()) {
                            String attrJson = IOUtil.readFileAsString(attrFile);
                            List<ActivityAttrDoc> activityAttrDocs = gson.fromJson(attrJson, new TypeToken<ArrayList<ActivityAttrDoc>>() {
                            }.getType());
                            moduleJsonDoc.setActivityAttrDocs(activityAttrDocs);
                        }
                    }
                }
                moduleDoc.setModuleName(moduleName);
                moduleDoc.setData(data);
                moduleDocs.add(moduleDoc);
            }
        }
        String json = gson.toJson(moduleDocs);
        // System.out.println("json = " + json);
        String htmlStr = IOUtil.isToString(this.getClass().getResourceAsStream("/routerDocIndex.html"));
        htmlStr = htmlStr.replace("@{routerDoc}", json);
        File outputFolder = new File(docFolder, "output");
        if (!outputFolder.exists() || outputFolder.isFile()) {
            outputFolder.mkdirs();
        }
        IOUtil.stringToFile(new File(outputFolder, "index.html"), htmlStr);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File subFile : files) {
                        deleteFile(subFile);
                    }
                }
            }
        }
    }

}
