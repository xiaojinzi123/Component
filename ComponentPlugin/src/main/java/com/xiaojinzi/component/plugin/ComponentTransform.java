package com.xiaojinzi.component.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ComponentTransform extends Transform {

    @Override
    public String getName() {
        return "ComponentPlugin";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        System.out.println("======================== transform ==========================");
        //当前是否是增量编译
        boolean isIncremental = transformInvocation.isIncremental();
        // 消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // 引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        // OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for(TransformInput input : inputs) {
            for(JarInput jarInput : input.getJarInputs()) {
                String jarName = jarInput.getFile().getPath();
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                /*JarFile jarFile = new JarFile(jarInput.getFile());
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();

                    String entryName = jarEntry.getName();
                    // 如果是目标工具类
                    if ("com/xiaojinzi/component/support/ASMUtil.class".equals(entryName)) {
                        System.out.println("jarName = " + jarName);
                        System.out.println("entryName = " + entryName);
                    }
                }*/
                File destJarFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "_" + jarInput.getFile().getName());
                if (destJarFile.exists()) {
                    destJarFile.delete();
                }

                JarFile jarFile = new JarFile(jarInput.getFile());
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

                JarOutputStream jarOutputStream = new JarOutputStream(
                        new FileOutputStream(destJarFile)
                );

                System.out.println("mmmmmmmmmm jarFile.size() = " + jarFile.size());

                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();

                    String entryName = jarEntry.getName();
                    System.out.println("mmmmmmmmmm-entryName = " + entryName);
                    // 如果是目标工具类
                    if ("com/xiaojinzi/component/support/ASMUtil.class".equals(entryName)) {
                        System.out.println("mmmmmmmmmm-jarName = " + jarName);
                        System.out.println("mmmmmmmmmm-找到了对应的类, 删除你!");
                    }else {
                    }
                    try {
                        jarOutputStream.putNextEntry(jarEntry);
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        // .copy(inputStream,jarOutputStream);
                        FileUtils
                    } catch (Exception e) {
                        System.out.println("jarEntry.isDirectory()：" + jarEntry.isDirectory());
                        System.out.println("错误信息：" + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                }
                jarOutputStream.close();
                // System.out.println("mmmmmmmmmm-destJarFile.getPath() = " + destJarFile.getPath());
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                // FileUtils.copyFile(destJarFile, dest);
                FileUtils.copyFile(jarInput.getFile(), dest);

            }
            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }
    }

}
