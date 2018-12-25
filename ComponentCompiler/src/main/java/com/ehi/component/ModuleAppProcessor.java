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
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ComponentUtil.MODULE_APP_ANNO_CLASS_NAME})
public class ModuleAppProcessor extends AbstractProcessor {

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
        mMessager.printMessage(Diagnostic.Kind.NOTE, "options = " + options);
        if (options != null) {
            componentHost = options.get("HOST");
        }

        if (componentHost == null || "".equals(componentHost)) {
            ErrorPrintUtil.printHostNull(mMessager);
            return;
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, "componentHost = " + componentHost);

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (componentHost == null || "".equals(componentHost)) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(set)) {

            Set<? extends Element> moduleAppElements = roundEnvironment.getElementsAnnotatedWith(EHiModuleAppAnno.class);

            mMessager.printMessage(Diagnostic.Kind.NOTE, " moduleApp.size = " + (moduleAppElements == null ? 0 : moduleAppElements.size()));

            parseAnnotation(moduleAppElements);

            createImpl();

            return true;
        }

        return false;
    }

    private List<Element> applicationList = new ArrayList<>();

    private void parseAnnotation(Set<? extends Element> moduleAppElements) {

        applicationList.clear();

        TypeMirror typeApplication = mElements.getTypeElement(ComponentConstants.EHIAPPLCATON).asType();

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
        String pkg = claName.substring(0, claName.lastIndexOf("."));

        //simpleName
        String cn = claName.substring(claName.lastIndexOf(".") + 1);

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
            e.printStackTrace();
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
        ClassName applicationName = ClassName.get(mElements.getTypeElement(ComponentConstants.APPLICATION));

        ParameterSpec parameterSpec = ParameterSpec.builder(applicationName, "application")
                .build();

        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onCreate")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addParameter(parameterSpec)
                .addModifiers(Modifier.PUBLIC);

        methodSpecBuilder.addStatement("super.onCreate(application)");
        methodSpecBuilder.addStatement("EHiRouter.register(getHost())");
        methodSpecBuilder.addStatement("com.ehi.component.impl.service.EHiCenterService.getInstance().register(getHost())");

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
        methodSpecBuilder.addStatement("com.ehi.component.impl.service.EHiCenterService.getInstance().unregister(getHost())");

        return methodSpecBuilder.build();
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
