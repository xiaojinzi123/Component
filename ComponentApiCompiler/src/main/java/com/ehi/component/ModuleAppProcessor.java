package com.ehi.component;

import com.ehi.component.anno.EHiModuleApp;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
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
@SupportedAnnotationTypes({"com.ehi.component.anno.EHiModuleApp"})
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
        if (options != null) {
            componentHost = options.get("HOST");
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, "ModuleAppProcessor.componentHost = " + componentHost);

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {

            Set<? extends Element> moduleAppElements = roundEnvironment.getElementsAnnotatedWith(EHiModuleApp.class);

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

        TypeMirror typeApplication = mElements.getTypeElement(EHiConstants.EHIAPPLCATON).asType();

        for (Element element : moduleAppElements) {

            TypeMirror tm = element.asType();

            if (!(element instanceof TypeElement)) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");

                continue;

            }

            if (!mTypes.isSubtype(tm, typeApplication)) {

                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " can't use 'EHiRouter' annotation");

                continue;

            }

            // 如果是一个 Application

            EHiModuleApp moduleApp = element.getAnnotation(EHiModuleApp.class);

            if (moduleApp == null) {

                continue;

            }

            applicationList.add(element);

            mMessager.printMessage(Diagnostic.Kind.NOTE, "moduleApplication = " + element);

        }
    }

    private void createImpl() {

        String claName = com.ehi.component.EHIComponentUtil.genHostModuleApplicationClassName(componentHost);

        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf("."));

        //simpleName
        String cn = claName.substring(claName.lastIndexOf(".") + 1);

        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(EHIComponentUtil.MODULE_APPLICATION_IMPL_CLASS_NAME));

        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec initMapMethod = generateInitMapMethod();

        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(initMapMethod)
                .build();

        try {
            JavaFile.builder(pkg, typeSpec
            ).indent("    ").build().writeTo(mFiler);
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
