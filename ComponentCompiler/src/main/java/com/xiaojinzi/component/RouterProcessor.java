package com.xiaojinzi.component;

import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.bean.RouterAnnoBean;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({com.xiaojinzi.component.ComponentUtil.ROUTER_ANNO_CLASS_NAME})
public class RouterProcessor extends BaseHostProcessor {

    private static final String ROUTER_BEAN_NAME = "com.xiaojinzi.component.bean.RouterBean";
    private static final String CUSTOMER_INTENT_CALL_CLASS_NAME = "com.xiaojinzi.component.bean.CustomerIntentCall";
    private static final String CUSTOMER_JUMP_CLASS_NAME = "com.xiaojinzi.component.bean.CustomerJump";

    private static final String NAME_OF_REQUEST = "request";

    private ClassName customerIntentCallClassName;
    private ClassName customerJumpClassName;

    private TypeElement routerBeanTypeElement;
    private ClassName exceptionClassName;
    private TypeElement intentTypeElement;
    private TypeMirror intentTypeMirror;
    private TypeMirror routerRequestTypeMirror;
    private TypeMirror parameterSupportTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;
    private TypeElement interceptorTypeElement;

    final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        interceptorTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.INTERCEPTOR_INTERFACE_CLASS_NAME);
        routerBeanTypeElement = mElements.getTypeElement(ROUTER_BEAN_NAME);
        final TypeElement exceptionTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.JAVA_EXCEPTION);
        exceptionClassName = ClassName.get(exceptionTypeElement);
        final TypeElement customerIntentCallTypeElement = mElements.getTypeElement(CUSTOMER_INTENT_CALL_CLASS_NAME);
        final TypeElement customerJumpTypeElement = mElements.getTypeElement(CUSTOMER_JUMP_CLASS_NAME);
        intentTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.ANDROID_INTENT);
        intentTypeMirror = intentTypeElement.asType();
        final TypeElement routerRequestTypeElement = mElements.getTypeElement(ComponentConstants.ROUTER_REQUEST_CLASS_NAME);
        routerRequestTypeMirror = routerRequestTypeElement.asType();
        customerIntentCallClassName = ClassName.get(customerIntentCallTypeElement);
        customerJumpClassName = ClassName.get(customerJumpTypeElement);
        final TypeElement parameterSupportTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.PARAMETERSUPPORT_CLASS_NAME);
        parameterSupportTypeMirror = parameterSupportTypeElement.asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            final Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(RouterAnno.class);
            parseAnno(routeElements);
            createRouterImpl();
            return true;
        }
        return false;
    }

    private final Map<String, RouterAnnoBean> routerMap = new HashMap<>();

    /**
     * 解析注解
     *
     * @param routeElements
     */
    private void parseAnno(Set<? extends Element> routeElements) {
        for (Element element : routeElements) {
            // 如果是一个Activity 才会走到这里
            final RouterAnno router = element.getAnnotation(RouterAnno.class);
            if (router == null || router.path() == null || router.path().isEmpty()) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + "：RouterAnno'path can;t be null or empty string");
                continue;
            }
            // 如果有host那就必须满足规范
            if (router.host() != null && !router.host().isEmpty() && router.host().contains("/")) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "the host path '" + router.host() + "' can't contains '/'");
            }
            if (routerMap.containsKey(getHostAndPath(router.host(), router.path()))) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + "：RouterAnno'path is alreay exist");
            }
            final RouterAnnoBean routerBean = new RouterAnnoBean();
            routerBean.setHost(router.host());
            routerBean.setPath(router.path());
            routerBean.setDesc(router.desc());
            routerBean.setRawType(element);
            routerBean.getInterceptors().clear();
            routerBean.getInterceptors().addAll(getImplClassName(router));
            if (router.interceptorNames() != null) {
                routerBean.getInterceptorNames().clear();
                for (String interceptorName : router.interceptorNames()) {
                    routerBean.getInterceptorNames().add(interceptorName);
                }
            }
            routerMap.put(getHostAndPath(router.host(), router.path()), routerBean);
        }
    }

    /**
     * 生成路由
     */
    private void createRouterImpl() {
        final String claName = com.xiaojinzi.component.ComponentUtil.genHostRouterClassName(componentHost);
        //pkg
        final String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        final String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        final ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.UIROUTER_IMPL_CLASS_NAME));
        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec initMapMethod = generateInitMapMethod();
        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(initMapMethod)
                .addJavadoc(componentHost + "业务模块的路由表\n")
                .build();
        try {
            JavaFile.builder(pkg, typeSpec)
                    .indent("    ")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

    private MethodSpec generateInitMapMethod() {
        TypeName returnType = TypeName.VOID;
        final MethodSpec.Builder initMapMethodSpecBuilder = MethodSpec.methodBuilder("initMap")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        initMapMethodSpecBuilder.addStatement("super.initMap()");
        routerMap.forEach(new BiConsumer<String, RouterAnnoBean>() {
            @Override
            public void accept(String key, RouterAnnoBean routerBean) {
                // 生成变量的名字,每一个变量代表每一个目标界面的配置对象
                String routerBeanName = "routerBean" + atomicInteger.incrementAndGet();
                // 生成 Activity 的调用代码
                generateActivityCall(routerBean, routerBeanName, initMapMethodSpecBuilder);
                // 生成静态方法的代码的调用
                generateStaticMethodCall(routerBean, routerBeanName, initMapMethodSpecBuilder);
                // 拦截器的代码的生成
                if (routerBean.getInterceptors() != null && !routerBean.getInterceptors().isEmpty()) {
                    String interceptorListName = "interceptorList" + atomicInteger.incrementAndGet();
                    initMapMethodSpecBuilder.addStatement("java.util.List<Class<? extends $T>> " + interceptorListName + " = new $T($L)", interceptorTypeElement, ArrayList.class, routerBean.getInterceptors().size());
                    for (String interceptorClassName : routerBean.getInterceptors()) {
                        initMapMethodSpecBuilder.addStatement("$N.add($T.class)", interceptorListName, ClassName.get(mElements.getTypeElement(interceptorClassName)));
                    }
                    initMapMethodSpecBuilder.addStatement("$N.setInterceptors($N)", routerBeanName, interceptorListName);
                }
                if (routerBean.getInterceptorNames() != null && !routerBean.getInterceptorNames().isEmpty()) {
                    String interceptorNameListName = "interceptorNameList" + atomicInteger.incrementAndGet();
                    initMapMethodSpecBuilder.addStatement("java.util.List<String> " + interceptorNameListName + " = new $T($L)", ArrayList.class, routerBean.getInterceptorNames().size());
                    for (String interceptorName : routerBean.getInterceptorNames()) {
                        initMapMethodSpecBuilder.addStatement("$N.add($S)", interceptorNameListName, interceptorName);
                    }
                    initMapMethodSpecBuilder.addStatement("$N.setInterceptorNames($N)", routerBeanName, interceptorNameListName);
                }
                initMapMethodSpecBuilder.addStatement("routerBeanMap.put($S,$N)", key, routerBeanName);
                initMapMethodSpecBuilder.addCode("\n");
            }
        });
        initMapMethodSpecBuilder.addJavadoc("初始化路由表的数据\n");
        return initMapMethodSpecBuilder.build();
    }

    private MethodSpec generateInitHostMethod() {

        TypeName returnType = mClassNameString;
        MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("getHost")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        openUriMethodSpecBuilder.addStatement("return $S", componentHost);
        return openUriMethodSpecBuilder.build();

    }

    private String getHostAndPath(String host, String path) {
        if (host == null || host.isEmpty()) {
            host = componentHost;
        }
        if (path != null && path.length() > 0 && path.charAt(0) != '/') {
            path = ComponentConstants.SEPARATOR + path;
        }
        return host + path;
    }

    private List<String> getImplClassName(RouterAnno anno) {
        List<String> implClassNames = new ArrayList<>();
        try {
            implClassNames.clear();
            //这里会报错，此时在catch中获取到拦截器的全类名
            final Class[] interceptors = anno.interceptors();
            // 这个循环其实不会走,我就随便写的,不过最好也不要删除
            for (Class interceptor : interceptors) {
                implClassNames.add(interceptor.getName());
            }
        } catch (MirroredTypesException e) {
            implClassNames.clear();
            final List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            for (TypeMirror typeMirror : typeMirrors) {
                implClassNames.add(typeMirror.toString());
            }
        }
        return implClassNames;
    }

    /**
     * 生成目标是 Activity 的调用
     *
     * @param routerBean
     * @param methodSpecBuilder
     */
    private void generateActivityCall(RouterAnnoBean routerBean, String routerBeanName, MethodSpec.Builder methodSpecBuilder) {
        if (!(routerBean.getRawType() instanceof TypeElement)) {
            return;
        }
        ClassName targetActivityClassName = ClassName.get((TypeElement) routerBean.getRawType());
        String commentStr = targetActivityClassName.toString();
        methodSpecBuilder.addCode("\n");
        methodSpecBuilder.addComment(NORMALLINE + commentStr + NORMALLINE);
        methodSpecBuilder.addCode("\n");
        methodSpecBuilder.addStatement("$T $N = new $T()", routerBeanTypeElement, routerBeanName, routerBeanTypeElement);
        methodSpecBuilder.addStatement("$N.setHost($S)", routerBeanName, routerBean.getHost());
        methodSpecBuilder.addStatement("$N.setPath($S)", routerBeanName, routerBean.getPath());
        methodSpecBuilder.addStatement("$N.setDesc($S)", routerBeanName, routerBean.getDesc());
        methodSpecBuilder.addStatement("$N.setTargetClass($T.class)", routerBeanName, targetActivityClassName);
    }

    /**
     * 目的是生成调用静态方法的实现
     *
     * @param routerBean        注解的实体对象
     * @param methodSpecBuilder 生成方法代码的 Builder
     * @return
     */
    private void generateStaticMethodCall(RouterAnnoBean routerBean, String routerBeanName, MethodSpec.Builder methodSpecBuilder) {
        if (!(routerBean.getRawType() instanceof ExecutableElement)) {
            return;
        }
        // 静态方法
        ExecutableElement executableElement = (ExecutableElement) routerBean.getRawType();
        // 获取声明这个方法的类的 TypeElement
        TypeElement declareClassType = (TypeElement) executableElement.getEnclosingElement();
        // 调用这个静态方法的全路径
        String customerIntentOrJumpPath = declareClassType.toString() + "." + executableElement.getSimpleName();
        // 注释的信息
        String commentStr = customerIntentOrJumpPath;
        // 获取自定义的静态方法的返回类型
        TypeMirror customerReturnType = executableElement.getReturnType();

        methodSpecBuilder.addCode("\n");
        methodSpecBuilder.addComment("---------------------------" + commentStr + "---------------------------");
        methodSpecBuilder.addCode("\n");
        methodSpecBuilder.addStatement("$T $N = new $T()", routerBeanTypeElement, routerBeanName, routerBeanTypeElement);
        methodSpecBuilder.addStatement("$N.setHost($S)", routerBeanName, routerBean.getHost());
        methodSpecBuilder.addStatement("$N.setPath($S)", routerBeanName, routerBean.getPath());
        methodSpecBuilder.addStatement("$N.setDesc($S)", routerBeanName, routerBean.getDesc());
        // 如果是自定义 Intent
        if (intentTypeMirror.equals(customerReturnType)) {

            MethodSpec.Builder jumpMethodBuilder = MethodSpec.methodBuilder("get")
                    .addParameter(TypeName.get(routerRequestTypeMirror), NAME_OF_REQUEST, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .addException(exceptionClassName)
                    .addModifiers(Modifier.PUBLIC);
            generateActualMethodCall(jumpMethodBuilder, executableElement, customerIntentOrJumpPath, true);
            jumpMethodBuilder.returns(TypeName.get(intentTypeElement.asType()));

            TypeSpec intentCallTypeSpec = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(customerIntentCallClassName)
                    .addMethod(
                            jumpMethodBuilder.build()
                    )
                    .build();
            // 添加一个匿名内部类
            methodSpecBuilder.addStatement("$N.setCustomerIntentCall($L)", routerBeanName, intentCallTypeSpec);
        } else { // 自定义跳转的

            MethodSpec.Builder jumpMethodBuilder = MethodSpec.methodBuilder("jump")
                    .addParameter(TypeName.get(routerRequestTypeMirror), NAME_OF_REQUEST, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .addException(exceptionClassName)
                    .addModifiers(Modifier.PUBLIC);

            generateActualMethodCall(jumpMethodBuilder, executableElement, customerIntentOrJumpPath, false);

            TypeSpec customerJumpTypeSpec = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(customerJumpClassName)
                    .addMethod(
                            jumpMethodBuilder.build()
                    )
                    .build();
            methodSpecBuilder.addStatement("$N.setCustomerJump($L)", routerBeanName, customerJumpTypeSpec);
        }
    }

    private void generateActualMethodCall(MethodSpec.Builder jumpMethodBuilder, ExecutableElement executableElement,
                                          String customerIntentOrJumpPath, boolean isReturnIntent) {
        // 获取这个方法的参数
        List<? extends VariableElement> parameters = executableElement.getParameters();
        // 参数调用的 sb
        StringBuilder parameterSB = new StringBuilder();
        if (parameters != null && !parameters.isEmpty()) {
            for (int i = 0, size = parameters.size(); i < size; i++) {
                if (i > 0) {
                    parameterSB.append(",");
                }
                // 拿到每一个参数
                VariableElement variableElement = parameters.get(i);
                TypeName parameterTypeName = ClassName.get(variableElement.asType());
                // 生成一个不重复的参数名字
                String parameterName = "paramater" + atomicInteger.incrementAndGet();
                // 如果要的是 request 对象
                if (variableElement.asType().equals(routerRequestTypeMirror)) {
                    parameterSB.append(NAME_OF_REQUEST);
                } else { // 其他类型的情况,是实现序列化的对象,这种时候我们直接要从 bundle 中获取
                    Utils.generateParameterCodeForRouter(
                            mTypes, variableElement, jumpMethodBuilder,
                            parameterSupportTypeMirror, parameterName,"request.bundle",
                            mClassNameString, serializableTypeMirror, parcelableTypeMirror
                    );
                    parameterSB.append(parameterName);
                }
            }
        }
        if (isReturnIntent) {
            jumpMethodBuilder.addStatement("return $N($N)", customerIntentOrJumpPath, parameterSB.toString());
        } else {
            jumpMethodBuilder.addStatement("$N($N)", customerIntentOrJumpPath, parameterSB.toString());
        }
    }

}
