package com.xiaojinzi.component.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.Set;

public class ASMUtilClassGen implements Opcodes {

    public static byte[] getBytes(Map<String, String> applicationMap, Map<String, String> interceptorMap,
            Map<String, String> routerMap, Map<String, String> routerDegradeMap, Map<String, String> serviceMap
            , Map<String, String> fragmentMap
    ) {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(
                V1_7, ACC_PUBLIC | ACC_SUPER,
                "com/xiaojinzi/component/support/ASMUtil", null,
                "java/lang/Object", null
        );

        writeStructureMethod(cw);
        writeApplicationMethod(cw, applicationMap);
        writeInterceptorMethod(cw, interceptorMap);
        writeRouterMethod(cw, routerMap);
        writeRouterDegradeMethod(cw, routerDegradeMap);
        writeServiceMethod(cw, serviceMap);
        writeFragmentMethod(cw, fragmentMap);
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static void writeStructureMethod(ClassVisitor cw) {
        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", "Lcom/xiaojinzi/component/support/ASMUtil;", null, label0, label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }

    private static void writeApplicationMethod(ClassVisitor cw, Map<String, String> applicationMap) {

        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "findModuleApplicationAsmImpl", "(Ljava/lang/String;)Lcom/xiaojinzi/component/application/IComponentHostApplication;", null, null);
        methodVisitor.visitCode();
        // 开始的标记
        Label labelStart = new Label();
        // 标记开始
        methodVisitor.visitLabel(labelStart);

        Set<Map.Entry<String, String>> entries = applicationMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            Label ifJumpLabel = new Label();
            // 拿到名称
            String name = entry.getKey();
            String classFullName = entry.getValue();
            String className = classFullName.substring(0, classFullName.lastIndexOf(".class"));
            String classPathName = "com/xiaojinzi/component/impl/application/" + className;

            methodVisitor.visitLdcInsn(name);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, ifJumpLabel);
            methodVisitor.visitTypeInsn(NEW, classPathName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, classPathName, "<init>", "()V", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(ifJumpLabel);
        }

        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);

        // 结束的标记
        Label labelEnd = new Label();
        methodVisitor.visitLabel(labelEnd);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0);
        methodVisitor.visitEnd();

    }

    private static void writeInterceptorMethod(ClassVisitor cw, Map<String, String> map) {

        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "findModuleInterceptorAsmImpl",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                "(Ljava/lang/String;)Ljava/lang/Class<Lcom/xiaojinzi/component/interceptor/IComponentHostInterceptor;>;",
                null);
        methodVisitor.visitCode();
        // 开始的标记
        Label labelStart = new Label();
        // 标记开始
        methodVisitor.visitLabel(labelStart);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            Label ifJumpLabel = new Label();
            // 拿到名称
            String name = entry.getKey();
            String classFullName = entry.getValue();
            String className = classFullName.substring(0, classFullName.lastIndexOf(".class"));
            String classPathName = "com/xiaojinzi/component/impl/interceptor/" + className;

            methodVisitor.visitLdcInsn(name);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, ifJumpLabel);
            methodVisitor.visitLdcInsn(Type.getType("L" + classPathName + ";"));
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(ifJumpLabel);
        }

        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);

        // 结束的标记
        Label labelEnd = new Label();
        methodVisitor.visitLabel(labelEnd);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0);
        methodVisitor.visitEnd();

    }

    private static void writeRouterMethod(ClassVisitor cw, Map<String, String> map) {

        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "findModuleRouterAsmImpl",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                "(Ljava/lang/String;)Ljava/lang/Class<Lcom/xiaojinzi/component/router/IComponentHostRouter;>;",
                null);
        methodVisitor.visitCode();
        // 开始的标记
        Label labelStart = new Label();
        // 标记开始
        methodVisitor.visitLabel(labelStart);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            Label ifJumpLabel = new Label();
            // 拿到名称
            String name = entry.getKey();
            String classFullName = entry.getValue();
            String className = classFullName.substring(0, classFullName.lastIndexOf(".class"));
            String classPathName = "com/xiaojinzi/component/impl/" + className;

            methodVisitor.visitLdcInsn(name);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, ifJumpLabel);
            methodVisitor.visitLdcInsn(Type.getType("L" + classPathName + ";"));
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(ifJumpLabel);
        }

        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);

        // 结束的标记
        Label labelEnd = new Label();
        methodVisitor.visitLabel(labelEnd);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0);
        methodVisitor.visitEnd();

    }

    private static void writeRouterDegradeMethod(ClassVisitor cw, Map<String, String> map) {

        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "findModuleRouterDegradeAsmImpl",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                "(Ljava/lang/String;)Ljava/lang/Class<Lcom/xiaojinzi/component/router/IComponentHostRouterDegrade;>;",
                null);
        methodVisitor.visitCode();
        // 开始的标记
        Label labelStart = new Label();
        // 标记开始
        methodVisitor.visitLabel(labelStart);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            Label ifJumpLabel = new Label();
            // 拿到名称
            String name = entry.getKey();
            String classFullName = entry.getValue();
            String className = classFullName.substring(0, classFullName.lastIndexOf(".class"));
            String classPathName = "com/xiaojinzi/component/impl/" + className;

            methodVisitor.visitLdcInsn(name);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, ifJumpLabel);
            methodVisitor.visitLdcInsn(Type.getType("L" + classPathName + ";"));
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(ifJumpLabel);
        }

        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);

        // 结束的标记
        Label labelEnd = new Label();
        methodVisitor.visitLabel(labelEnd);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0);
        methodVisitor.visitEnd();

    }

    private static void writeServiceMethod(ClassVisitor cw, Map<String, String> map) {

        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "findModuleServiceAsmImpl",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                "(Ljava/lang/String;)Ljava/lang/Class<Lcom/xiaojinzi/component/service/IComponentHostService;>;",
                null);
        methodVisitor.visitCode();
        // 开始的标记
        Label labelStart = new Label();
        // 标记开始
        methodVisitor.visitLabel(labelStart);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            Label ifJumpLabel = new Label();
            // 拿到名称
            String name = entry.getKey();
            String classFullName = entry.getValue();
            String className = classFullName.substring(0, classFullName.lastIndexOf(".class"));
            String classPathName = "com/xiaojinzi/component/impl/service/" + className;

            methodVisitor.visitLdcInsn(name);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, ifJumpLabel);
            methodVisitor.visitLdcInsn(Type.getType("L" + classPathName + ";"));
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(ifJumpLabel);
        }

        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);

        // 结束的标记
        Label labelEnd = new Label();
        methodVisitor.visitLabel(labelEnd);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0);
        methodVisitor.visitEnd();

    }

    private static void writeFragmentMethod(ClassVisitor cw, Map<String, String> map) {

        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "findModuleFragmentAsmImpl",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                "(Ljava/lang/String;)Ljava/lang/Class<Lcom/xiaojinzi/component/fragment/IComponentHostFragment;>;",
                null);
        methodVisitor.visitCode();
        // 开始的标记
        Label labelStart = new Label();
        // 标记开始
        methodVisitor.visitLabel(labelStart);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            Label ifJumpLabel = new Label();
            // 拿到名称
            String name = entry.getKey();
            String classFullName = entry.getValue();
            String className = classFullName.substring(0, classFullName.lastIndexOf(".class"));
            String classPathName = "com/xiaojinzi/component/impl/fragment/" + className;

            methodVisitor.visitLdcInsn(name);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, ifJumpLabel);
            methodVisitor.visitLdcInsn(Type.getType("L" + classPathName + ";"));
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(ifJumpLabel);
        }

        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);

        // 结束的标记
        Label labelEnd = new Label();
        methodVisitor.visitLabel(labelEnd);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0);
        methodVisitor.visitEnd();

    }

}