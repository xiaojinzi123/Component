package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.ServiceAnno;

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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * 负责处理 {@link ServiceAnno}
 */
@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({com.xiaojinzi.component.ComponentUtil.SERVICE_ANNO_CLASS_NAME})
public class ServiceProcessor extends BaseHostProcessor {

    private static final String NAME_OF_APPLICATION = "application";

    private TypeElement typeElementLifecycle;

    private ClassName classNameLifecycle;
    private ClassName classNameServiceContainer;
    private ClassName lazyLoadClassName;
    private ClassName singletonLazyLoadClassName;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeElementLifecycle = mElements.getTypeElement(ComponentConstants.BASE_LIFECYCLE_INTERFACE_CLASS_NAME);
        classNameLifecycle = ClassName.get(typeElementLifecycle);
        final TypeElement typeElementServiceContainer = mElements.getTypeElement(ComponentConstants.SERVICE_CLASS_NAME);
        classNameServiceContainer = ClassName.get(typeElementServiceContainer);
        final TypeElement service1TypeElement = mElements.getTypeElement(ComponentConstants.CALLABLE_CLASS_NAME);
        final TypeElement service2TypeElement = mElements.getTypeElement(ComponentConstants.SINGLETON_CALLABLE_CLASS_NAME);
        lazyLoadClassName = ClassName.get(service1TypeElement);
        singletonLazyLoadClassName = ClassName.get(service2TypeElement);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (componentHost == null || componentHost.isEmpty()) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> annoElements = roundEnvironment.getElementsAnnotatedWith(ServiceAnno.class);
            parseAnnotation(annoElements);
            createImpl();
            return true;
        }
        return false;
    }

    private final List<Element> annoElementList = new ArrayList<>();

    private void parseAnnotation(Set<? extends Element> annoElements) {
        annoElementList.clear();
        for (Element element : annoElements) {
            // 如果是一个 Service
            final ServiceAnno anno = element.getAnnotation(ServiceAnno.class);
            if (anno == null) {
                continue;
            }
            annoElementList.add(element);
        }
    }

    private void createImpl() {
        String claName = ComponentUtil.genHostServiceClassName(componentHost);
        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.SERVICE_IMPL_CLASS_NAME));
        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec onCreateMethod = generateOnCreateMethod();
        MethodSpec onDestroyMethod = generateOnDestroyMethod();
        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                .addAnnotation(mClassNameKeep)
                .addAnnotation(mClassNameComponentGeneratedAnno)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(onCreateMethod)
                .addMethod(onDestroyMethod)
                .build();

        try {
            JavaFile.builder(pkg, typeSpec
            ).indent("    ").build().writeTo(mFiler);
        } catch (IOException ignore) {
            // ignore
        }
    }

    private MethodSpec generateOnCreateMethod() {
        TypeName returnType = TypeName.VOID;
        ClassName applicationName = ClassName.get(mElements.getTypeElement(ComponentConstants.ANDROID_APPLICATION));
        ParameterSpec parameterSpec = ParameterSpec.builder(applicationName, NAME_OF_APPLICATION)
                .addModifiers(Modifier.FINAL)
                .build();
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onModuleCreate")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addParameter(parameterSpec)
                .addModifiers(Modifier.PUBLIC);
        methodSpecBuilder.addStatement("super.onModuleCreate(application)");
        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                String serviceImplCallPath = null;
                TypeMirror serviceImplTypeMirror = null;
                TypeName serviceImplTypeName = null;
                boolean isExtendModuleLifecycle = false;
                boolean isMethodParameterEmpty = false;
                if (element instanceof ExecutableElement) {
                    // 注解在方法上了
                    ExecutableElement methodElement = (ExecutableElement) element;
                    serviceImplTypeMirror = methodElement.getReturnType();
                    serviceImplTypeName = TypeName.get(serviceImplTypeMirror);
                    isMethodParameterEmpty = methodElement.getParameters().size() == 0;
                    // 获取声明这个方法的类的 TypeElement
                    TypeElement declareClassType = (TypeElement) methodElement.getEnclosingElement();
                    // 调用这个静态方法的全路径
                    serviceImplCallPath = declareClassType.toString() + "." + methodElement.getSimpleName();
                } else {
                    String serviceImplClassName = element.toString();
                    serviceImplTypeMirror = mElements.getTypeElement(serviceImplClassName).asType();
                    serviceImplTypeName = TypeName.get(serviceImplTypeMirror);
                }
                isExtendModuleLifecycle = mTypes.isSubtype(serviceImplTypeMirror, typeElementLifecycle.asType());

                ServiceAnno anno = element.getAnnotation(ServiceAnno.class);
                String implName = "implName" + atomicInteger.incrementAndGet();
                MethodSpec.Builder getOrRawMethodBuilder = MethodSpec.methodBuilder(anno.singleTon() ? "getRaw" : "get")
                        .addAnnotation(Override.class)
                        .addModifiers(anno.singleTon() ? Modifier.PROTECTED : Modifier.PUBLIC);
                String serviceName = "service" + atomicInteger.incrementAndGet();
                if (serviceImplCallPath == null) {
                    boolean haveDefaultConstructor = isHaveDefaultConstructor(element.toString());
                    getOrRawMethodBuilder
                            .addStatement("$T $N = new $T($N)", serviceImplTypeName, serviceName, serviceImplTypeName, (haveDefaultConstructor ? "" : NAME_OF_APPLICATION));
                } else {
                    getOrRawMethodBuilder
                            .addStatement("$T $N = $N($N)", serviceImplTypeName, serviceName, serviceImplCallPath, (isMethodParameterEmpty ? "" : NAME_OF_APPLICATION));
                }
                if (isExtendModuleLifecycle) {
                    getOrRawMethodBuilder
                            .beginControlFlow("if ($N instanceof $T)", serviceName, typeElementLifecycle)
                            .addStatement("$T moduleLifecycle = ($T)$N", typeElementLifecycle, typeElementLifecycle, serviceName)
                            .addStatement("moduleLifecycle.onModuleCreate($N)", NAME_OF_APPLICATION)
                            .endControlFlow();
                }
                getOrRawMethodBuilder
                        .addStatement("return $N", serviceName)
                        .returns(serviceImplTypeName);
                TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(anno.singleTon() ? singletonLazyLoadClassName : lazyLoadClassName, serviceImplTypeName))
                        .addMethod(getOrRawMethodBuilder.build())
                        .build();
                methodSpecBuilder.addStatement("$T $N = $L", lazyLoadClassName, implName, innerTypeSpec);

                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                for (String interServiceClassName : interServiceClassNames) {
                    methodSpecBuilder.addStatement("$T.register($T.class,$N)", classNameServiceContainer, ClassName.get(mElements.getTypeElement(interServiceClassName)), implName);
                }

            }
        });

        return methodSpecBuilder.build();
    }

    private MethodSpec generateOnDestroyMethod() {
        TypeName returnType = TypeName.VOID;
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onModuleDestroy")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        methodSpecBuilder.addStatement("super.onModuleDestroy()");
        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                ServiceAnno anno = element.getAnnotation(ServiceAnno.class);
                TypeMirror serviceImplTypeMirror = null;
                boolean isExtendModuleLifecycle = false;
                if (element instanceof ExecutableElement) {
                    // 注解在方法上了
                    ExecutableElement methodElement = (ExecutableElement) element;
                    serviceImplTypeMirror = methodElement.getReturnType();
                } else {
                    String serviceImplClassName = element.toString();
                    serviceImplTypeMirror = mElements.getTypeElement(serviceImplClassName).asType();
                }
                isExtendModuleLifecycle = mTypes.isSubtype(serviceImplTypeMirror, typeElementLifecycle.asType());

                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                for (String interServiceClassNameStr : interServiceClassNames) {
                    ClassName interServiceClassName = ClassName.get(mElements.getTypeElement(interServiceClassNameStr));
                    String serviceName = "service" + atomicInteger.incrementAndGet();
                    if (isExtendModuleLifecycle) {
                        methodSpecBuilder.addStatement("$T $N = $T.unregister($T.class)", interServiceClassName, serviceName, classNameServiceContainer, interServiceClassName);
                        methodSpecBuilder
                                .beginControlFlow("if ($N instanceof $T)", serviceName, typeElementLifecycle)
                                .addStatement("$T moduleLifecycle = ($T)$N", typeElementLifecycle, typeElementLifecycle, serviceName)
                                .addStatement("moduleLifecycle.onModuleDestroy()")
                                .endControlFlow();
                    } else {
                        methodSpecBuilder.addStatement("$T.unregister($T.class)", classNameServiceContainer, interServiceClassName);
                    }
                }
            }
        });
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

    /**
     * 获取注解中的目标 Service 接口的全类名
     *
     * @param anno
     * @return
     */
    private List<String> getInterServiceClassNames(ServiceAnno anno) {
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

    /**
     * 是否有默认的构造器
     *
     * @param className
     * @return
     */
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
