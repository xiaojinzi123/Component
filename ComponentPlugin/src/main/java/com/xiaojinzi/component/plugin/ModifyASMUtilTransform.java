package com.xiaojinzi.component.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.utils.FileUtils;
import com.xiaojinzi.component.plugin.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;

public class ModifyASMUtilTransform extends BaseTransform {

    /**
     * value 是 class 的名称
     * "user" -> "UserModuleApplicationGeneratedDefault" or "user" -> "UserModuleApplicationGenerated"
     */
    private Map<String, String> applicationMap = new HashMap<>();

    /**
     * value 是 class 的名称
     * "user" -> "UserInterceptorGenerated"
     */
    private Map<String, String> interceptorMap = new HashMap<>();

    /**
     * value 是 class 的名称
     * "user" -> "UserRouterGenerated"
     */
    private Map<String, String> routerMap = new HashMap<>();

    /**
     * value 是 class 的名称
     * "user" -> "UserRouterDegradeGenerated"
     */
    private Map<String, String> routerDegradeMap = new HashMap<>();

    /**
     * value 是 class 的名称
     * "user" -> "UserServiceGenerated"
     */
    private Map<String, String> serviceMap = new HashMap<>();

    /**
     * value 是 class 的名称
     * "user" -> "UserFragmentGenerated"
     */
    private Map<String, String> fragmentMap = new HashMap<>();

    @Override
    public String getName() {
        return "ComponentPlugin";
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        filterAllNames(transformInvocation);

        // 消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                File destJarFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "_" + jarInput.getFile().getName());
                if (destJarFile.exists()) {
                    destJarFile.delete();
                }

                JarFile jarFile = new JarFile(jarInput.getFile());
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

                JarOutputStream jarOutputStream = new JarOutputStream(
                        new FileOutputStream(destJarFile)
                );
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    String entryName = jarEntry.getName();
                    // 如果是目标工具类, 就换成手动生成的
                    if ("com/xiaojinzi/component/support/ASMUtil.class".equals(entryName)) {

                        byte[] bytes = ASMUtilClassGen.getBytes(
                                applicationMap, interceptorMap, routerMap,
                                routerDegradeMap, serviceMap, fragmentMap
                        );

                        // 生成到桌面用来测试
                        /*FileOutputStream fileOutputStream = new FileOutputStream(new File("/Users/xiaojinzi/Desktop/test.class"));
                        fileOutputStream.write(bytes);
                        fileOutputStream.close();*/

                        JarEntry asmUtiJarEntry = new JarEntry(jarEntry.getName());
                        asmUtiJarEntry.setSize(bytes.length);
                        CRC32 crc = new CRC32();
                        crc.update(bytes);
                        asmUtiJarEntry.setCrc(crc.getValue());
                        jarOutputStream.putNextEntry(asmUtiJarEntry);
                        jarOutputStream.write(bytes);

                    } else {
                        jarOutputStream.putNextEntry(jarEntry);
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        IOUtil.readAndWrite(inputStream, jarOutputStream);
                        inputStream.close();
                    }
                    jarOutputStream.closeEntry();
                }
                jarOutputStream.close();
                FileUtils.copyFile(destJarFile, dest);
                // 删除文件
                destJarFile.delete();
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                File directoryInputFile = directoryInput.getFile();
                // printFile(directoryInputFile);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyDirectory(directoryInputFile, dest);
            }
        }

    }

    /**
     * 找到所有 Application 的生成类
     *
     * @param transformInvocation
     * @throws IOException
     */
    private void filterAllNames(TransformInvocation transformInvocation) throws IOException {
        applicationMap.clear();
        interceptorMap.clear();
        routerMap.clear();
        routerDegradeMap.clear();
        serviceMap.clear();
        fragmentMap.clear();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                JarFile jarFile = new JarFile(jarInput.getFile());
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    String entryName = jarEntry.getName();
                    String className = entryName.substring(entryName.lastIndexOf("/") + 1);
                    doFilterApplicationName(className);
                    doFilterInterceptorName(className);
                    doFilterRouterName(className);
                    doFilterRouterDegradeName(className);
                    doFilterServiceName(className);
                    doFilterFragmentName(className);
                }
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                recursiveFile(directoryInput.getFile(), new IFileRecursive() {
                    @Override
                    public void accept(File file) {
                        // xxx.class
                        String className = file.getName();
                        doFilterApplicationName(className);
                        doFilterInterceptorName(className);
                        doFilterRouterName(className);
                        doFilterRouterDegradeName(className);
                        doFilterServiceName(className);
                        doFilterFragmentName(className);
                    }
                });
            }
        }
    }

    private void doFilterApplicationName(String className) {
        if (className.endsWith("ModuleApplicationGeneratedDefault.class") ||
                className.endsWith("ModuleApplicationGenerated.class")) {
            // 拿到 host 的名称
            String hostName = className.substring(0, className.indexOf("ModuleApplicationGenerated"));
            // 如果是 default 的, 需要看 map 中是否已经存在了
            if (className.endsWith("ModuleApplicationGeneratedDefault.class")) {
                // 如果不存在才能进去
                if (!applicationMap.containsKey(hostName)) {
                    applicationMap.put(hostName, className);
                }
            } else {
                applicationMap.put(hostName, className);
            }
        }
    }

    private void doFilterInterceptorName(String className) {
        if (className.endsWith("InterceptorGenerated.class")) {
            // 拿到 host 的名称
            String hostName = className.substring(0, className.indexOf("InterceptorGenerated"));
            interceptorMap.put(hostName, className);
        }
    }

    private void doFilterRouterName(String className) {
        if (className.endsWith("RouterGenerated.class")) {
            // 拿到 host 的名称
            String hostName = className.substring(0, className.indexOf("RouterGenerated"));
            routerMap.put(hostName, className);
        }
    }

    private void doFilterRouterDegradeName(String className) {
        if (className.endsWith("RouterDegradeGenerated.class")) {
            // 拿到 host 的名称
            String hostName = className.substring(0, className.indexOf("RouterDegradeGenerated"));
            routerDegradeMap.put(hostName, className);
        }
    }

    private void doFilterServiceName(String className) {
        if (className.endsWith("ServiceGenerated.class")) {
            // 拿到 host 的名称
            String hostName = className.substring(0, className.indexOf("ServiceGenerated"));
            serviceMap.put(hostName, className);
        }
    }

    private void doFilterFragmentName(String className) {
        if (className.endsWith("FragmentGenerated.class")) {
            // 拿到 host 的名称
            String hostName = className.substring(0, className.indexOf("FragmentGenerated"));
            fragmentMap.put(hostName, className);
        }
    }

    private void recursiveFile(File file, IFileRecursive fileRecursive) {
        if (file.isFile()) {
            fileRecursive.accept(file);
        } else {
            File[] files = file.listFiles();
            for (File subFile : files) {
                if (subFile.isFile()) {
                    fileRecursive.accept(subFile);
                } else {
                    recursiveFile(subFile, fileRecursive);
                }
            }
        }
    }

    private void printFile(File file) {
        if (file.isFile()) {
            // System.out.println("printFile = " + file.getPath());
        } else {
            // System.out.println("printFolder = " + file.getPath());
            File[] files = file.listFiles();
            for (File subFile : files) {
                if (subFile.isFile()) {
                    // System.out.println("printFile = " + subFile.getPath());
                } else {
                    printFile(subFile);
                }
            }
        }
    }

}
