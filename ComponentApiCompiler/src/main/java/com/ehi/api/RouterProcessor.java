package com.ehi.api;

import com.ehi.api.anno.EHiRouter;
import com.ehi.api.bean.RouterBean;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.ehi.api.anno.EHiRouter"})
public class RouterProcessor extends AbstractProcessor {

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

        mMessager.printMessage(Diagnostic.Kind.NOTE, "RouterProcessor.componentHost = " + componentHost);

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (CollectionUtils.isNotEmpty(set)) {

            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(EHiRouter.class);

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

        TypeMirror typeActivity = mElements.getTypeElement(EHiConstants.ACTIVITY).asType();

        for (Element element : routeElements) {

            mMessager.printMessage(Diagnostic.Kind.NOTE, "element == " + element.toString());

            TypeMirror tm = element.asType();

            if (!(element instanceof TypeElement)) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");

                continue;

            }

            if (!mTypes.isSubtype(tm, typeActivity)) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " can't use 'EHiRouter' annotation");

                continue;

            }

            // 如果是一个Activity

            EHiRouter router = element.getAnnotation(EHiRouter.class);

            if (router == null) {

                continue;

            }

            if (router.value() == null || "".equals(router.value())) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + "：EHiRouter'value can;t be null or empty string");
                continue;

            }

            if (routerMap.containsKey(router.value())) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + "：EHiRouter'value is alreay exist");
                continue;

            }

            RouterBean routerBean = new RouterBean();
            routerBean.setDesc(router.desc());
            routerBean.setPath(router.value());
            routerBean.setPriority(router.priority());
            routerBean.setRawType(element);

            routerMap.put(router.value(), routerBean);

            mMessager.printMessage(Diagnostic.Kind.NOTE, "router.value() = " + router.value() + ",Activity = " + element);

        }

    }

    /**
     * 生成路由
     */
    private void createRouterImpl() {

        String claName = EHIComponentUtil.genHostUIRouterClassName(componentHost);

        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf("."));

        //simpleName
        String cn = claName.substring(claName.lastIndexOf(".") + 1);

        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(EHIComponentUtil.UIROUTER_IMPL_CLASS_NAME));

        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec initMapMethod = generateInitMapMethod();

        try {
            JavaFile.builder(pkg, TypeSpec.classBuilder(cn)
                    //.addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.FINAL)
                    .superclass(superClass)
                    .addMethod(initHostMethod)
                    .addMethod(initMapMethod)
                    .build()
            ).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MethodSpec generateInitMapMethod() {
        TypeName returnType = TypeName.VOID;

        final MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("initMap")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        openUriMethodSpecBuilder.addStatement("super.initMap()");

        routerMap.forEach(new BiConsumer<String, RouterBean>() {
            @Override
            public void accept(String key, RouterBean routerBean) {

                openUriMethodSpecBuilder.addStatement(
                        "routerMap" + ".put($S,$T.class)", key,
                        ClassName.get((TypeElement) routerBean.getRawType()));

            }
        });

        return openUriMethodSpecBuilder.build();
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


}
