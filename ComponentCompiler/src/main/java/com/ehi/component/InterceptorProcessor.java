package com.ehi.component;

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
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
@SupportedAnnotationTypes({ComponentUtil.INTERCEPTOR_ANNO_CLASS_NAME})
public class InterceptorProcessor extends BaseHostProcessor {

    private TypeElement interceptorUtilTypeElement;
    private TypeElement routerTypeElement;
    private TypeElement interceptorBeanTypeElement;
    private TypeElement nullableTypeElement;
    private ClassName nullableClassName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        interceptorUtilTypeElement = mElements.getTypeElement(ComponentConstants.EHIINTERCEPTOR_UTIL_CLASS_NAME);
        routerTypeElement = mElements.getTypeElement(ComponentConstants.EHIROUTER_CLASS_NAME);
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

            Set<? extends Element> moduleAppElements = roundEnvironment.getElementsAnnotatedWith(EHiInterceptorAnno.class);

            parseAnnotation(moduleAppElements);

            createImpl();

            return true;
        }

        return false;

    }

    private List<InterceptorBean> mElementList = new ArrayList<>();

    private void parseAnnotation(Set<? extends Element> moduleAppElements) {

        mElementList.clear();

        TypeMirror typeInterceptor = mElements.getTypeElement(ComponentConstants.EHIINTERCEPTOR_INTERFACE_CLASS_NAME).asType();

        for (Element element : moduleAppElements) {

            TypeMirror tm = element.asType();

            if (!(element instanceof TypeElement)) { // 比如是类

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");

                continue;

            }

            if (!mTypes.isSubtype(tm, typeInterceptor)) { // 比如是拦截器接口的之类

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " can't use 'EHiModuleAppAnno' annotation");

                continue;

            }

            // 如果是一个 Application

            EHiInterceptorAnno anno = element.getAnnotation(EHiInterceptorAnno.class);

            if (anno == null) {
                continue;
            }

            mElementList.add(new InterceptorBean(element, anno.priority()));

        }

        /*mElementList.sort(new Comparator<InterceptorBean>() {
            @Override
            public int compare(InterceptorBean o1, InterceptorBean o2) {
                if (o1.priority == o2.priority) {
                    return 0;
                } else if (o1.priority > o2.priority) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });*/

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
        MethodSpec interceptorListMethod = generateInterceptorListMethod();

        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(interceptorListMethod)
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
     * @Nullable
     * @Override public List<EHiInterceptorBean> interceptorList() {
     * return null;
     * }
     */
    private MethodSpec generateInterceptorListMethod() {

        TypeName returnType = ParameterizedTypeName.get(mClassNameList, TypeName.get(interceptorBeanTypeElement.asType()));
        ClassName applicationName = ClassName.get(mElements.getTypeElement(ComponentConstants.APPLICATION));

        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("interceptorList")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addAnnotation(nullableClassName)
                .addModifiers(Modifier.PUBLIC);

        if (mElementList.size() == 0) {
            methodSpecBuilder.addStatement("return null");
        } else {

            final AtomicInteger atomicInteger = new AtomicInteger();

            methodSpecBuilder.addStatement("$T<$T> list = new $T<>()", mClassNameList, interceptorBeanTypeElement, mClassNameArrayList);

            mElementList.forEach(new Consumer<InterceptorBean>() {
                @Override
                public void accept(InterceptorBean interceptorBean) {

                    String implClassName = interceptorBean.element.toString();
                    TypeElement implTypeElement = mElements.getTypeElement(implClassName);
                    methodSpecBuilder.addStatement("list.add(new $T($T.get($T.class),$L))", interceptorBeanTypeElement, interceptorUtilTypeElement, implTypeElement, interceptorBean.priority);

                    // methodSpecBuilder.addStatement("$T.addRouterInterceptor($T.get($T.class))", routerTypeElement, interceptorUtilTypeElement, implTypeElement);
                    // com.ehi.component.impl.interceptor.EHiRouterInterceptorUtil.get($T.class)

                }
            });


            methodSpecBuilder.addStatement("return list");

        /*mElementList.forEach(new Consumer<InterceptorBean>() {
            @Override
            public void accept(InterceptorBean interceptorBean) {

                String implClassName = interceptorBean.element.toString();
                TypeElement implTypeElement = mElements.getTypeElement(implClassName);

                methodSpecBuilder.addStatement("$T.addRouterInterceptor($T.get($T.class))", routerTypeElement, interceptorUtilTypeElement, implTypeElement);
                // com.ehi.component.impl.interceptor.EHiRouterInterceptorUtil.get($T.class)

            }
        });*/
        }

        return methodSpecBuilder.build();
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
