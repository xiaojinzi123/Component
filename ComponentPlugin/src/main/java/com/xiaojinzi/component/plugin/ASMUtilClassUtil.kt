package com.xiaojinzi.component.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.Modifier

object ASMUtilClassUtil {

    fun getClassBytes(
        moduleNameMap: Map<String, String>,
        interceptorMap: Map<String, String>,
        routerMap: Map<String, String>,
        routerDegradeMap: Map<String, String>,
        serviceMap: Map<String, String>,
        fragmentMap: Map<String, String>,
    ): ByteArray {

        val classPool = ClassPool.getDefault()
        val listClass = classPool.get("java.util.List")
        val asmUtilClassPath = "com.xiaojinzi.component.support.ASMUtil"
        val asmUtilClass = classPool.getOrNull(asmUtilClassPath)
            ?: classPool.makeClass(asmUtilClassPath)

        if (asmUtilClass.isFrozen) {
            println("${ComponentPlugin.TAG}, asmUtilClass is frozen, will call defrost() method")
            asmUtilClass.defrost()
            // return asmUtilClass.toBytecode()
        }

        getModuleNamesMethod(listClass, asmUtilClass, moduleNameMap)

        findModuleApplicationAsmImplMethod(classPool, asmUtilClass, moduleNameMap)

        findModuleInterceptorAsmImplMethod(classPool, asmUtilClass, interceptorMap)

        findModuleRouterAsmImplMethod(classPool, asmUtilClass, routerMap)

        findModuleRouterDegradeAsmImplMethod(classPool, asmUtilClass, routerDegradeMap)

        findModuleServiceAsmImplMethod(classPool, asmUtilClass, serviceMap)

        findModuleFragmentAsmImplMethod(classPool, asmUtilClass, fragmentMap)

        return asmUtilClass.toBytecode()
    }

    private fun findModuleFragmentAsmImplMethod(
        classPool: ClassPool,
        asmUtilClass: CtClass,
        serviceMap: Map<String, String>
    ) {
        // ========================= findModuleFragmentAsmImpl ========================= start

        val interfaceIInterceptorLifecycle =
            classPool.makeInterface("com.xiaojinzi.component.fragment.IComponentHostFragment")
        val ctMethod = CtMethod(
            interfaceIInterceptorLifecycle,
            "findModuleFragmentAsmImpl",
            arrayOf(classPool.get("java.lang.String")),
            asmUtilClass,
        )
        ctMethod.modifiers =
            Modifier.PUBLIC or Modifier.STATIC
        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        if (serviceMap.isEmpty()) {
            methodBodySb.append("return null;")
        } else {
            serviceMap.entries.forEachIndexed { index, entry ->
                val targetClassFullName = entry.value.removeSuffix(
                    suffix = ".class"
                )
                classPool.makeClass(targetClassFullName).apply {
                    this.addConstructor(
                        CtConstructor(
                            arrayOf(),
                            this
                        ).apply {
                            modifiers = Modifier.PUBLIC
                        }
                    )
                }
                if (index != 0) {
                    methodBodySb.append(" else ")
                }
                methodBodySb.append("if (\"${entry.key}\".equalsIgnoreCase($1)) {")
                methodBodySb.append(
                    "return new ${targetClassFullName}();"
                )
                methodBodySb.append("}")
            }
            methodBodySb.append(" else {return null;}")
        }
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString(),
        )
        asmUtilClass.getDeclaredMethods("findModuleFragmentAsmImpl").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

    private fun findModuleServiceAsmImplMethod(
        classPool: ClassPool,
        asmUtilClass: CtClass,
        serviceMap: Map<String, String>
    ) {
        // ========================= findModuleServiceAsmImpl ========================= start

        val interfaceIInterceptorLifecycle =
            classPool.makeInterface("com.xiaojinzi.component.service.IComponentHostService")
        val ctMethod = CtMethod(
            interfaceIInterceptorLifecycle,
            "findModuleServiceAsmImpl",
            arrayOf(classPool.get("java.lang.String")),
            asmUtilClass,
        )
        ctMethod.modifiers =
            Modifier.PUBLIC or Modifier.STATIC
        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        if (serviceMap.isEmpty()) {
            methodBodySb.append("return null;")
        } else {
            serviceMap.entries.forEachIndexed { index, entry ->
                val targetClassFullName = entry.value.removeSuffix(
                    suffix = ".class"
                )
                classPool.makeClass(targetClassFullName).apply {
                    this.addConstructor(
                        CtConstructor(
                            arrayOf(),
                            this
                        ).apply {
                            modifiers = Modifier.PUBLIC
                        }
                    )
                }
                if (index != 0) {
                    methodBodySb.append(" else ")
                }
                methodBodySb.append("if (\"${entry.key}\".equalsIgnoreCase($1)) {")
                methodBodySb.append(
                    "return new ${targetClassFullName}();"
                )
                methodBodySb.append("}")
            }
            methodBodySb.append(" else {return null;}")
        }
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString(),
        )
        asmUtilClass.getDeclaredMethods("findModuleServiceAsmImpl").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

    private fun findModuleRouterDegradeAsmImplMethod(
        classPool: ClassPool,
        asmUtilClass: CtClass,
        routerDegradeMap: Map<String, String>
    ) {
        // ========================= findModuleRouterDegradeAsmImpl ========================= start

        val interfaceIInterceptorLifecycle =
            classPool.makeInterface("com.xiaojinzi.component.router.IComponentHostRouterDegrade")
        val ctMethod = CtMethod(
            interfaceIInterceptorLifecycle,
            "findModuleRouterDegradeAsmImpl",
            arrayOf(classPool.get("java.lang.String")),
            asmUtilClass,
        )
        ctMethod.modifiers =
            Modifier.PUBLIC or Modifier.STATIC
        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        if (routerDegradeMap.isEmpty()) {
            methodBodySb.append("return null;")
        } else {
            routerDegradeMap.entries.forEachIndexed { index, entry ->
                val targetClassFullName = entry.value.removeSuffix(
                    suffix = ".class"
                )
                classPool.makeClass(targetClassFullName).apply {
                    this.addConstructor(
                        CtConstructor(
                            arrayOf(),
                            this
                        ).apply {
                            modifiers = Modifier.PUBLIC
                        }
                    )
                }
                if (index != 0) {
                    methodBodySb.append(" else ")
                }
                methodBodySb.append("if (\"${entry.key}\".equalsIgnoreCase($1)) {")
                methodBodySb.append(
                    "return new ${targetClassFullName}();"
                )
                methodBodySb.append("}")
            }
            methodBodySb.append(" else {return null;}")
        }
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString(),
        )
        asmUtilClass.getDeclaredMethods("findModuleRouterDegradeAsmImpl").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

    private fun findModuleRouterAsmImplMethod(
        classPool: ClassPool,
        asmUtilClass: CtClass,
        routerMap: Map<String, String>
    ) {
        // ========================= findModuleInterceptorAsmImpl ========================= start

        val interfaceIInterceptorLifecycle =
            classPool.makeInterface("com.xiaojinzi.component.router.IComponentHostRouter")
        val ctMethod = CtMethod(
            interfaceIInterceptorLifecycle,
            "findModuleRouterAsmImpl",
            arrayOf(classPool.get("java.lang.String")),
            asmUtilClass,
        )
        ctMethod.modifiers =
            Modifier.PUBLIC or Modifier.STATIC
        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        if (routerMap.isEmpty()) {
            methodBodySb.append("return null;")
        } else {
            routerMap.entries.forEachIndexed { index, entry ->
                val targetClassFullName = entry.value.removeSuffix(
                    suffix = ".class"
                )
                classPool.makeClass(targetClassFullName).apply {
                    this.addConstructor(
                        CtConstructor(
                            arrayOf(),
                            this
                        ).apply {
                            modifiers = Modifier.PUBLIC
                        }
                    )
                }
                if (index != 0) {
                    methodBodySb.append(" else ")
                }
                methodBodySb.append("if (\"${entry.key}\".equalsIgnoreCase($1)) {")
                methodBodySb.append(
                    "return new ${targetClassFullName}();"
                )
                methodBodySb.append("}")
            }
            methodBodySb.append(" else {return null;}")
        }
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString(),
        )
        asmUtilClass.getDeclaredMethods("findModuleRouterAsmImpl").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

    private fun findModuleInterceptorAsmImplMethod(
        classPool: ClassPool,
        asmUtilClass: CtClass,
        interceptorMap: Map<String, String>
    ) {
        // ========================= findModuleInterceptorAsmImpl ========================= start

        val interfaceIInterceptorLifecycle =
            classPool.makeInterface("com.xiaojinzi.component.interceptor.IComponentHostInterceptor")
        val ctMethod = CtMethod(
            interfaceIInterceptorLifecycle,
            "findModuleInterceptorAsmImpl",
            arrayOf(classPool.get("java.lang.String")),
            asmUtilClass,
        )
        ctMethod.modifiers =
            Modifier.PUBLIC or Modifier.STATIC
        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        if (interceptorMap.isEmpty()) {
            methodBodySb.append("return null;")
        } else {
            interceptorMap.entries.forEachIndexed { index, entry ->
                val targetClassFullName = entry.value.removeSuffix(
                    suffix = ".class"
                )
                classPool.makeClass(targetClassFullName).apply {
                    this.addConstructor(
                        CtConstructor(
                            arrayOf(),
                            this
                        ).apply {
                            modifiers = Modifier.PUBLIC
                        }
                    )
                }
                if (index != 0) {
                    methodBodySb.append(" else ")
                }
                methodBodySb.append("if (\"${entry.key}\".equalsIgnoreCase($1)) {")
                methodBodySb.append(
                    "return new ${targetClassFullName}();"
                )
                methodBodySb.append("}")
            }
            methodBodySb.append(" else {return null;}")
        }
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString(),
        )
        asmUtilClass.getDeclaredMethods("findModuleInterceptorAsmImpl").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

    private fun findModuleApplicationAsmImplMethod(
        classPool: ClassPool,
        asmUtilClass: CtClass,
        moduleNameMap: Map<String, String>
    ) {
        // ========================= findModuleApplicationAsmImpl ========================= start

        val interfaceIModuleLifecycle =
            classPool.makeInterface("com.xiaojinzi.component.application.IComponentHostApplication")
        val ctMethod = CtMethod(
            interfaceIModuleLifecycle,
            "findModuleApplicationAsmImpl",
            arrayOf(classPool.get("java.lang.String")),
            asmUtilClass,
        )
        ctMethod.modifiers =
            Modifier.PUBLIC or Modifier.STATIC
        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        if (moduleNameMap.isEmpty()) {
            methodBodySb.append("return null;")
        } else {
            moduleNameMap.entries.forEachIndexed { index, entry ->
                val targetClassFullName = entry.value.removeSuffix(
                    suffix = ".class"
                )
                classPool.makeClass(targetClassFullName).apply {
                    this.addConstructor(
                        CtConstructor(
                            arrayOf(),
                            this
                        ).apply {
                            modifiers = Modifier.PUBLIC
                        }
                    )
                }
                if (index != 0) {
                    methodBodySb.append(" else ")
                }
                methodBodySb.append("if (\"${entry.key}\".equalsIgnoreCase($1)) {")
                methodBodySb.append(
                    "return new ${targetClassFullName}();"
                )
                methodBodySb.append("}")
            }
            methodBodySb.append(" else {return null;}")
        }
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString(),
        )
        asmUtilClass.getDeclaredMethods("findModuleApplicationAsmImpl").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

    private fun getModuleNamesMethod(
        listClass: CtClass?,
        asmUtilClass: CtClass,
        moduleNameMap: Map<String, String>
    ) {
        // ========================= getModuleNames ========================= start

        val ctMethod = CtMethod(listClass, "getModuleNames", emptyArray(), asmUtilClass)
        ctMethod.modifiers = Modifier.PUBLIC or Modifier.STATIC
        ctMethod.genericSignature = "(Ljava/util/List<Ljava/lang/String;>;)V"

        val methodBodySb = StringBuilder()
        methodBodySb.append("{")
        methodBodySb.append("java.util.List list = new java.util.ArrayList();")
        moduleNameMap.keys.forEach {
            methodBodySb.append("list.add(\"$it\");")
        }
        methodBodySb.append("return list;")
        methodBodySb.append("}")
        ctMethod.setBody(
            methodBodySb.toString()
        )
        asmUtilClass.getDeclaredMethods("getModuleNames").forEach {
            asmUtilClass.removeMethod(it)
        }
        asmUtilClass.addMethod(ctMethod)
    }

}