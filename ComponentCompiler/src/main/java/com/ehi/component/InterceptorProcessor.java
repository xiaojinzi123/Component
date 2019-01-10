package com.ehi.component;

import com.ehi.component.anno.EHiGlobalInterceptorAnno;
import com.ehi.component.anno.EHiInterceptorAnno;
import com.ehi.component.bean.InterceptorBean;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
import javax.tools.Diagnostic;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ComponentUtil.GLOBAL_INTERCEPTOR_ANNO_CLASS_NAME, ComponentUtil.INTERCEPTOR_ANNO_CLASS_NAME})
public class InterceptorProcessor extends BaseHostProcessor {

    private TypeElement interceptorUtilTypeElement;
    private TypeElement interceptorBeanTypeElement;
    private TypeElement nullableTypeElement;
    private ClassName nullableClassName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        interceptorUtilTypeElement = mElements.getTypeElement(ComponentConstants.EHIINTERCEPTOR_UTIL_CLASS_NAME);
        interceptorBeanTypeElement = mElements.getTypeElement(ComponentConstants.EHIINTERCEPTOR_BEAN_CLASS_NAME);
        nullableTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_ANNOTATION_NULLABLE);
        nullableClassName = ClassName.get(nullableTypeElement);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (componentHost == null || "".equals(componentHost)) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(set)) {

            Set<? extends Element> globalInterceptorElements = roundEnvironment.getElementsAnnotatedWith(EHiGlobalInterceptorAnno.class);
            Set<? extends Element> interceptorElements = roundEnvironment.getElementsAnnotatedWith(EHiInterceptorAnno.class);

            parseGlobalInterceptAnnotation(globalInterceptorElements);
            parseNormalInterceptAnnotation(interceptorElements);

            createImpl();

            return true;
        }

        return false;

    }

    private List<InterceptorBean> mGlobalInterceptElementList = new ArrayList<>();
    private Map<String, InterceptorBean> mNormalInterceptElementMap = new HashMap<>();

    private void parseGlobalInterceptAnnotation(Set<? extends Element> globalInterceptorElements) {

        mGlobalInterceptElementList.clear();

        // 拦截器的接口
        TypeMirror typeInterceptor = mElements.getTypeElement(ComponentConstants.EHIINTERCEPTOR_INTERFACE_CLASS_NAME).asType();

        for (Element element : globalInterceptorElements) {

            TypeMirror tm = element.asType();

            if (!(element instanceof TypeElement)) { // 比如是类
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");
                continue;
            }

            if (!mTypes.isSubtype(tm, typeInterceptor)) { // 比如是拦截器接口的之类
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " must implementation interface '" + ComponentConstants.EHIINTERCEPTOR_INTERFACE_CLASS_NAME + "'");
                continue;
            }

            EHiGlobalInterceptorAnno anno = element.getAnnotation(EHiGlobalInterceptorAnno.class);

            if (anno == null) {
                continue;
            }

            mGlobalInterceptElementList.add(new InterceptorBean(InterceptorBean.GLOBAL_INTERCEPTOR, element, anno.priority(), null));

        }

    }

    private void parseNormalInterceptAnnotation(Set<? extends Element> normalInterceptorElements) {

        mNormalInterceptElementMap.clear();

        // 拦截器的接口
        TypeMirror typeInterceptor = mElements.getTypeElement(ComponentConstants.EHIINTERCEPTOR_INTERFACE_CLASS_NAME).asType();

        for (Element element : normalInterceptorElements) {

            TypeMirror tm = element.asType();

            if (!(element instanceof TypeElement)) { // 比如是类
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");
                continue;
            }

            if (!mTypes.isSubtype(tm, typeInterceptor)) { // 比如是拦截器接口的之类
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " must implementation interface '" + ComponentConstants.EHIINTERCEPTOR_INTERFACE_CLASS_NAME + "'");
                continue;
            }

            EHiInterceptorAnno anno = element.getAnnotation(EHiInterceptorAnno.class);

            if (anno == null) {
                continue;
            }

            if (mNormalInterceptElementMap.containsKey(anno.value())) {
                throw new RuntimeException("the interceptor's name '" + anno.value() + "' is exist");
            }

            mNormalInterceptElementMap.put(anno.value(), new InterceptorBean(InterceptorBean.NORMAL_INTERCEPTOR, element, 0, anno.value()));

        }

    }

    private void createImpl() {

        String claName = ComponentUtil.genHostInterceptorClassName(componentHost);

        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf("."));

        //simpleName
        String cn = claName.substring(claName.lastIndexOf(".") + 1);

        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.INTERCEPTOR_IMPL_CLASS_NAME));

        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec globalInterceptorListMethod = generateGlobalInterceptorListMethod();
        MethodSpec normalInterceptorListMethod = generateNormalInterceptorInitMapMethod();

        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(globalInterceptorListMethod)
                .addMethod(normalInterceptorListMethod)
                .build();

        try {
            JavaFile
                    .builder(pkg, typeSpec)
                    .indent("    ")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return
     */
    private MethodSpec generateGlobalInterceptorListMethod() {

        TypeName returnType = ParameterizedTypeName.get(mClassNameList, TypeName.get(interceptorBeanTypeElement.asType()));

        final MethodSpec.Builder globalInterceptorListMethodSpecBuilder = MethodSpec.methodBuilder("globalInterceptorList")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addAnnotation(nullableClassName)
                .addModifiers(Modifier.PUBLIC);

        if (mGlobalInterceptElementList.size() == 0) {
            globalInterceptorListMethodSpecBuilder.addStatement("return null");
        } else {

            globalInterceptorListMethodSpecBuilder.addStatement("$T<$T> list = new $T<>()", mClassNameList, interceptorBeanTypeElement, mClassNameArrayList);

            mGlobalInterceptElementList.forEach(new Consumer<InterceptorBean>() {
                @Override
                public void accept(InterceptorBean interceptorBean) {
                    String implClassName = interceptorBean.element.toString();
                    TypeElement implTypeElement = mElements.getTypeElement(implClassName);
                    globalInterceptorListMethodSpecBuilder.addStatement("list.add(new $T($T.get($T.class),$L))", interceptorBeanTypeElement, interceptorUtilTypeElement, implTypeElement, interceptorBean.priority);
                }
            });

            globalInterceptorListMethodSpecBuilder.addStatement("return list");

        }

        return globalInterceptorListMethodSpecBuilder.build();
    }

    private MethodSpec generateNormalInterceptorInitMapMethod() {

        final MethodSpec.Builder normalInterceptorInitMapMethodSpecBuilder = MethodSpec.methodBuilder("initInterceptorMap")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED);

        normalInterceptorInitMapMethodSpecBuilder.addStatement("super.initInterceptorMap()");

        if (mNormalInterceptElementMap.size() != 0) {

            mNormalInterceptElementMap.values().forEach(new Consumer<InterceptorBean>() {
                @Override
                public void accept(InterceptorBean interceptorBean) {
                    String implClassName = interceptorBean.element.toString();
                    TypeElement implTypeElement = mElements.getTypeElement(implClassName);
                    normalInterceptorInitMapMethodSpecBuilder.addStatement("interceptorMap.put($S, $T.class)", interceptorBean.name, implTypeElement);
                }
            });

        }

        return normalInterceptorInitMapMethodSpecBuilder.build();
    }

    private MethodSpec generateInitHostMethod() {

        TypeName returnType = TypeName.get(mTypeElementString.asType());

        MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("getHost")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        openUriMethodSpecBuilder.addStatement("return $S", componentHost);

        return openUriMethodSpecBuilder.build();
    }

}
