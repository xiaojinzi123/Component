package com.ehi.component;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
 * 负责处理 {@link EHiModuleAppAnno}
 */
@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ComponentUtil.MODULE_APP_ANNO_CLASS_NAME})
public class ModuleAppProcessor extends BaseHostProcessor {

    private TypeElement eHiCenterInterceptorTypeElement;
    private TypeElement ehiCenterServiceTypeElement;
    private TypeElement ehiRouterTypeElement;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        eHiCenterInterceptorTypeElement = mElements.getTypeElement(ComponentConstants.EHICENTERINTERCEPTOR_CLASS_NAME);
        ehiCenterServiceTypeElement = mElements.getTypeElement(ComponentConstants.EHICENTERSERVICE_CLASS_NAME);
        ehiRouterTypeElement = mElements.getTypeElement(ComponentConstants.EHIROUTER_CLASS_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (componentHost == null || componentHost.isEmpty()) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> moduleAppElements = roundEnvironment.getElementsAnnotatedWith(EHiModuleAppAnno.class);
            parseAnnotation(moduleAppElements);
            createImpl();
            return true;
        }
        return false;
    }

    private List<Element> applicationList = new ArrayList<>();

    private void parseAnnotation(Set<? extends Element> moduleAppElements) {
        applicationList.clear();
        TypeMirror typeApplication = mElements.getTypeElement(ComponentConstants.EHIAPPLCATON_INTERFACE_CLASS_NAME).asType();
        for (Element element : moduleAppElements) {
            TypeMirror tm = element.asType();
            if (!(element instanceof TypeElement)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");
                continue;
            }
            if (!mTypes.isSubtype(tm, typeApplication)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " can't use 'EHiModuleAppAnno' annotation");
                continue;
            }
            // 如果是一个 Application
            EHiModuleAppAnno moduleApp = element.getAnnotation(EHiModuleAppAnno.class);
            if (moduleApp == null) {
                continue;
            }
            applicationList.add(element);
            mMessager.printMessage(Diagnostic.Kind.NOTE, "moduleApplication = " + element);
        }
    }

    private void createImpl() {
        String claName = ComponentUtil.genHostModuleApplicationClassName(componentHost);
        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.MODULE_APPLICATION_IMPL_CLASS_NAME));
        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec initMapMethod = generateInitMapMethod();
        MethodSpec onCreateMethod = generateOnCreateMethod();
        MethodSpec onDestoryMethod = generateOnDestoryMethod();
        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(initMapMethod)
                .addMethod(onCreateMethod)
                .addMethod(onDestoryMethod)
                .build();
        try {
            JavaFile
                    .builder(pkg, typeSpec)
                    .indent("    ")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

    private MethodSpec generateInitMapMethod() {
        TypeName returnType = TypeName.VOID;
        final MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("initList")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        openUriMethodSpecBuilder.addStatement("super.initList()");
        applicationList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                openUriMethodSpecBuilder.addStatement(
                        "moduleAppList" + ".add(new $T())",
                        ClassName.get((TypeElement) element)
                );
            }
        });
        return openUriMethodSpecBuilder.build();
    }

    private MethodSpec generateOnCreateMethod() {
        TypeName returnType = TypeName.VOID;
        ClassName applicationName = ClassName.get(mElements.getTypeElement(ComponentConstants.ANDROID_APPLICATION));
        ParameterSpec parameterSpec = ParameterSpec.builder(applicationName, "application")
                .build();
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onCreate")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addParameter(parameterSpec)
                .addModifiers(Modifier.PUBLIC);
        methodSpecBuilder.addStatement("super.onCreate(application)");
        methodSpecBuilder.addStatement("$T.register(getHost())", ehiRouterTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().register(getHost())", ehiCenterServiceTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().register(getHost())", eHiCenterInterceptorTypeElement);
        return methodSpecBuilder.build();
    }

    private MethodSpec generateOnDestoryMethod() {
        TypeName returnType = TypeName.VOID;
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onDestory")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        methodSpecBuilder.addStatement("super.onDestory()");
        methodSpecBuilder.addStatement("EHiRouter.unregister(getHost())");
        methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", ehiCenterServiceTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", eHiCenterInterceptorTypeElement);
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
