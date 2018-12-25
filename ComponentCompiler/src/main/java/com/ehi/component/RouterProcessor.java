package com.ehi.component;

import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.bean.RouterBean;
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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ComponentUtil.ROUTER_ANNO_CLASS_NAME})
public class RouterProcessor extends AbstractProcessor {

    private static final String ROUTER_BEAN_NAME = "com.ehi.component.bean.EHiRouterBean";
    private static final String INTERCEPTOR_UTIL_NAME = "com.ehi.component.impl.EHiRouterInterceptorUtil";
    private static final String COMPONENT_CONFIG_NAME = "com.ehi.component.ComponentConfig";

    private TypeMirror typeString;

    private Filer mFiler;
    private Messager mMessager;
    private Types mTypes;
    private Elements mElements;

    // 在每一个 module 中配置的 HOST 的信息
    private String componentHost = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mFiler = processingEnv.getFiler();
        mMessager = processingEnvironment.getMessager();
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();

        typeString = mElements.getTypeElement("java.lang.String").asType();

        Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            componentHost = options.get("HOST");
        }

        if (componentHost == null || "".equals(componentHost)) {
            ErrorPrintUtil.printHostNull(mMessager);
            return;
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, "RouterProcessor.componentHost = " + componentHost);

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (CollectionUtils.isNotEmpty(set)) {

            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(EHiRouterAnno.class);

            mMessager.printMessage(Diagnostic.Kind.NOTE, " >>> size = " + (routeElements == null ? 0 : routeElements.size()));

            parseRouterAnno(routeElements);

            createRouterImpl();

            return true;
        }

        return false;

    }

    private Map<String, RouterBean> routerMap = new HashMap<>();

    /**
     * 解析注解
     *
     * @param routeElements
     */
    private void parseRouterAnno(Set<? extends Element> routeElements) {

        TypeMirror typeActivity = mElements.getTypeElement(ComponentConstants.ACTIVITY).asType();

        for (Element element : routeElements) {

            mMessager.printMessage(Diagnostic.Kind.NOTE, "element == " + element.toString());

            TypeMirror tm = element.asType();

            // 必须标记的是一种类型的元素
            if (!(element instanceof TypeElement)) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");

                continue;

            }

            // 必须标记在 Activity 上
            if (!mTypes.isSubtype(tm, typeActivity)) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " can't use 'EHiRouterAnno' annotation");

                continue;

            }

            // 如果是一个Activity 才会走到这里

            EHiRouterAnno router = element.getAnnotation(EHiRouterAnno.class);

            if (router == null) {

                continue;

            }

            if (router.value() == null || "".equals(router.value())) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + "：EHiRouterAnno'value can;t be null or empty string");
                continue;

            }

            // 如果有host那就必须满足规范
            if (router.host() == null || "".equals(router.host())) {
            } else {
                if (router.host().contains("/")) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "the host value '" + router.host() + "' can't contains '/'");
                }
            }

            if (routerMap.containsKey(getHostAndPath(router.host(), router.value()))) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + "：EHiRouterAnno'value is alreay exist");
                continue;

            }

            RouterBean routerBean = new RouterBean();
            routerBean.setHost(router.host());
            routerBean.setPath(router.value());
            routerBean.setDesc(router.desc());
            routerBean.setRawType(element);
            routerBean.getInterceptors().addAll(getImplClassName(router));

            routerMap.put(getHostAndPath(router.host(), router.value()), routerBean);

            mMessager.printMessage(Diagnostic.Kind.NOTE, "router.value() = " + router.value() + ",Activity = " + element);

        }

    }

    /**
     * 生成路由
     */
    private void createRouterImpl() {

        String claName = ComponentUtil.genHostUIRouterClassName(componentHost);

        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf("."));

        //simpleName
        String cn = claName.substring(claName.lastIndexOf(".") + 1);

        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.UIROUTER_IMPL_CLASS_NAME));

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
            e.printStackTrace();
        }

    }

    private MethodSpec generateInitMapMethod() {
        TypeName returnType = TypeName.VOID;

        final MethodSpec.Builder initMapMethodSpecBuilder = MethodSpec.methodBuilder("initMap")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        initMapMethodSpecBuilder.addStatement("super.initMap()");

        final AtomicInteger atomicInteger = new AtomicInteger();

        routerMap.forEach(new BiConsumer<String, com.ehi.component.bean.RouterBean>() {
            @Override
            public void accept(String key, RouterBean routerBean) {

                ClassName targetClassName = ClassName.get((TypeElement) routerBean.getRawType());
                String routerBeanName = "routerBean" + atomicInteger.incrementAndGet();

                initMapMethodSpecBuilder.addCode("\n");
                initMapMethodSpecBuilder.addComment("---------------------------" + targetClassName.toString() + "---------------------------");
                initMapMethodSpecBuilder.addCode("\n");

                initMapMethodSpecBuilder.addStatement("$N $N = new $N()", ROUTER_BEAN_NAME, routerBeanName, ROUTER_BEAN_NAME);
                initMapMethodSpecBuilder.addStatement("$N.host = $S", routerBeanName, routerBean.getHost());
                initMapMethodSpecBuilder.addStatement("$N.path = $S", routerBeanName, routerBean.getPath());
                initMapMethodSpecBuilder.addStatement("$N.desc = $S", routerBeanName, routerBean.getDesc());
                initMapMethodSpecBuilder.addStatement("$N.targetClass = $T.class", routerBeanName, targetClassName);
                if (routerBean.getInterceptors() != null && routerBean.getInterceptors().size() > 0) {
                    initMapMethodSpecBuilder.addStatement("$N.interceptors = new $T()", routerBeanName,ArrayList.class);
                    for (String interceptorClassName : routerBean.getInterceptors()) {
                        initMapMethodSpecBuilder.addStatement("$N.interceptors.add($T.class)", routerBeanName, ClassName.get(mElements.getTypeElement(interceptorClassName)));
                    }
                }
                initMapMethodSpecBuilder.addStatement("routerBeanMap.put($S,$N)", key, routerBeanName);

                initMapMethodSpecBuilder.addCode("\n");
                //initMapMethodSpecBuilder.addStatement("routerMap" + ".put($S,$T.class)", key, targetClassName);
                //initMapMethodSpecBuilder.addStatement("isNeedLoginMap" + ".put($T.class,$L)", targetClassName, routerBean.isNeedLogin());


            }
        });

        initMapMethodSpecBuilder.addJavadoc("初始化路由表的数据\n");

        return initMapMethodSpecBuilder.build();
    }

    private MethodSpec generateInitHostMethod() {

        TypeName returnType = TypeName.get(typeString);

        MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("getHost")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        openUriMethodSpecBuilder.addStatement("return $S", componentHost);

        return openUriMethodSpecBuilder.build();
    }

    private String getHostAndPath(String host, String path) {

        if (host == null || "".equals(host)) {
            host = componentHost;
        }

        if (path != null && path.length() > 0 && path.charAt(0) != '/') {
            path = "/" + path;
        }

        return host + path;

    }

    private List<String> getImplClassName(EHiRouterAnno anno) {
        List<String> implClassNames = new ArrayList<>();
        try {
            implClassNames.clear();
            Class[] interceptors = anno.interceptors();
            for (Class interceptor : interceptors) {
                implClassNames.add(interceptor.getName());
            }
        } catch (MirroredTypesException e) {
            implClassNames.clear();
            List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            for (TypeMirror typeMirror : typeMirrors) {
                implClassNames.add(typeMirror.toString());
            }
        }
        return implClassNames;
    }

    private boolean isHaveDefaultConstructor(String interceptorClassName) {
        // 实现类的类型
        TypeElement typeElementInterceptorImpl = mElements.getTypeElement(interceptorClassName);
        String constructorName = typeElementInterceptorImpl.getSimpleName().toString() + ("()");
        List<? extends Element> enclosedElements = typeElementInterceptorImpl.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.toString().equals(constructorName)) {
                return true;
            }
        }
        return false;
    }

}
