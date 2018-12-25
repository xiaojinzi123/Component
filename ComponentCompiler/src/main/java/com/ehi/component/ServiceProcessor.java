package com.ehi.component;

import com.ehi.component.anno.EHiServiceAnno;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ComponentUtil.SERVICE_ANNO_CLASS_NAME})
public class ServiceProcessor extends AbstractProcessor {

    private static final String SERVICE_CONTAINER_NAME = "com.ehi.component.service.EHiService";

    private static final String SERVICE_SEPER_NAME1 = "com.ehi.component.service.IServiceLoad";
    private static final String SERVICE_SEPER_NAME2 = "com.ehi.component.service.SingletonService";

    private TypeMirror typeString;

    private TypeElement typeElementServiceContainer;

    private ClassName classNameServiceContainer;

    private TypeElement typeElementService1;
    private TypeElement typeElementService2;

    private ClassName classNameService1;
    private ClassName classNameService2;

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

        typeElementServiceContainer = mElements.getTypeElement(SERVICE_CONTAINER_NAME);

        classNameServiceContainer = ClassName.get(typeElementServiceContainer);

        typeElementService1 = mElements.getTypeElement(SERVICE_SEPER_NAME1);
        typeElementService2 = mElements.getTypeElement(SERVICE_SEPER_NAME2);

        classNameService1 = ClassName.get(typeElementService1);
        classNameService2 = ClassName.get(typeElementService2);

        Map<String, String> options = processingEnv.getOptions();

        if (options != null) {
            componentHost = options.get("HOST");
        }

        if (componentHost == null || "".equals(componentHost)) {
            ErrorPrintUtil.printHostNull(mMessager);
            return;
        }


    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (componentHost == null || "".equals(componentHost)) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(set)) {

            Set<? extends Element> annoElements = roundEnvironment.getElementsAnnotatedWith(EHiServiceAnno.class);

            parseAnnotation(annoElements);

            createImpl();

            return true;
        }

        return false;
    }

    private List<Element> annoElementList = new ArrayList<>();

    private void parseAnnotation(Set<? extends Element> annoElements) {

        annoElementList.clear();

        TypeMirror typeApplication = mElements.getTypeElement(ComponentConstants.EHIAPPLCATON).asType();

        for (Element element : annoElements) {

            TypeMirror tm = element.asType();

            if (!(element instanceof TypeElement)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, element + " is not a 'TypeElement' ");
                continue;
            }

            // 如果是一个 Service

            EHiServiceAnno anno = element.getAnnotation(EHiServiceAnno.class);

            if (anno == null) {

                continue;

            }

            annoElementList.add(element);

            mMessager.printMessage(Diagnostic.Kind.NOTE, "serviceImpl = " + element);

        }
    }

    private void createImpl() {

        String claName = ComponentUtil.genHostServiceClassName(componentHost);

        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf("."));

        //simpleName
        String cn = claName.substring(claName.lastIndexOf(".") + 1);

        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.SERVICE_IMPL_CLASS_NAME));

        MethodSpec initHostMethod = generateInitHostMethod();
//        MethodSpec initMapMethod = generateInitMapMethod();
        MethodSpec onCreateMethod = generateOnCreateMethod();
        MethodSpec onDestoryMethod = generateOnDestoryMethod();

        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(onCreateMethod)
                .addMethod(onDestoryMethod)
                .build();

        try {
            JavaFile.builder(pkg, typeSpec
            ).indent("    ").build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private MethodSpec generateOnCreateMethod() {

        TypeName returnType = TypeName.VOID;
        ClassName applicationName = ClassName.get(mElements.getTypeElement(ComponentConstants.APPLICATION));

        ParameterSpec parameterSpec = ParameterSpec.builder(applicationName, "application")
                .addModifiers(Modifier.FINAL)
                .build();

        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onCreate")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addParameter(parameterSpec)
                .addModifiers(Modifier.PUBLIC);

        methodSpecBuilder.addStatement("super.onCreate(application)");

        final AtomicInteger atomicInteger = new AtomicInteger();

        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                String serviceImplClassName = element.toString();
                TypeElement serviceImplTypeElement = mElements.getTypeElement(serviceImplClassName);
                EHiServiceAnno anno = element.getAnnotation(EHiServiceAnno.class);
                boolean haveDefaultConstructor = isHaveDefaultConstructor(element.toString());
                String implName = "implName" + atomicInteger.incrementAndGet();

                if (anno.singleTon()) {

                    TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(ParameterizedTypeName.get(classNameService2, TypeName.get(serviceImplTypeElement.asType())))
                            .addMethod(
                                    MethodSpec.methodBuilder("getRaw")
                                            .addAnnotation(Override.class)
                                            .addModifiers(Modifier.PROTECTED)
                                            .addStatement("return new $T($N)", serviceImplTypeElement, (haveDefaultConstructor ? "" : "application"))
                                            .returns(TypeName.get(element.asType()))
                                            .build()
                            )
                            .build();
                    methodSpecBuilder.addStatement("$T $N = $L", classNameService1, implName, innerTypeSpec);

                } else {
                    TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(ParameterizedTypeName.get(classNameService1, TypeName.get(serviceImplTypeElement.asType())))
                            .addMethod(
                                    MethodSpec.methodBuilder("get")
                                            .addAnnotation(Override.class)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addStatement("return new $T($N)", serviceImplTypeElement, (haveDefaultConstructor ? "" : "application"))
                                            .returns(TypeName.get(element.asType()))
                                            .build()
                            )
                            .build();
                    methodSpecBuilder.addStatement("$T $N = $L", classNameService1, implName, innerTypeSpec);
                }

                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                for (String interServiceClassName : interServiceClassNames) {
                    methodSpecBuilder.addStatement("$T.register($T.class,$N)", classNameServiceContainer, ClassName.get(mElements.getTypeElement(interServiceClassName)), implName);
                }

            }
        });

        return methodSpecBuilder.build();
    }

    private MethodSpec generateOnDestoryMethod() {

        TypeName returnType = TypeName.VOID;

        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onDestory")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        methodSpecBuilder.addStatement("super.onDestory()");

        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                EHiServiceAnno anno = element.getAnnotation(EHiServiceAnno.class);
                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                for (String interServiceClassName : interServiceClassNames) {
                    methodSpecBuilder.addStatement("$T.unregister($T.class)", classNameServiceContainer, ClassName.get(mElements.getTypeElement(interServiceClassName)));
                }
            }
        });

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

    /**
     * 获取注解中的目标 Service 接口的全类名
     *
     * @param anno
     * @return
     */
    private List<String> getInterServiceClassNames(EHiServiceAnno anno) {
        List<String> implClassNames = new ArrayList<>();
        try {
            implClassNames.clear();
            Class[] interceptors = anno.value();
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

    private boolean isHaveDefaultConstructor(String className) {
        // 实现类的类型
        TypeElement typeElementClassImpl = mElements.getTypeElement(className);
        String constructorName = typeElementClassImpl.getSimpleName().toString() + ("()");
        List<? extends Element> enclosedElements = typeElementClassImpl.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.toString().equals(constructorName)) {
                return true;
            }
        }
        return false;
    }

}
