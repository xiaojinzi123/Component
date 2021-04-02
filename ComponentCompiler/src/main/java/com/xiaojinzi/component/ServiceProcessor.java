package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.ConditionalAnno;
import com.xiaojinzi.component.anno.ServiceAnno;
import com.xiaojinzi.component.anno.ServiceDecoratorAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
@SupportedAnnotationTypes({
        ComponentUtil.SERVICE_ANNO_CLASS_NAME,
        ComponentUtil.SERVICE_DECORATOR_ANNO_CLASS_NAME,
})
public class ServiceProcessor extends BaseHostProcessor {

    private static final String NAME_OF_APPLICATION = "application";

    private ClassName classNameServiceContainer;
    private ClassName lazyLoadClassName;
    private ClassName singletonLazyLoadClassName;
    private ClassName decoratorCallableClassName;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        final TypeElement typeElementServiceContainer = mElements.getTypeElement(ComponentConstants.SERVICE_CLASS_NAME);
        classNameServiceContainer = ClassName.get(typeElementServiceContainer);
        final TypeElement service1TypeElement = mElements.getTypeElement(ComponentConstants.CALLABLE_CLASS_NAME);
        final TypeElement service2TypeElement = mElements.getTypeElement(ComponentConstants.SINGLETON_CALLABLE_CLASS_NAME);
        final TypeElement service3TypeElement = mElements.getTypeElement(ComponentConstants.DECORATOR_CALLABLE_CLASS_NAME);
        lazyLoadClassName = ClassName.get(service1TypeElement);
        singletonLazyLoadClassName = ClassName.get(service2TypeElement);
        decoratorCallableClassName = ClassName.get(service3TypeElement);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (componentHost == null || componentHost.isEmpty()) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> serviceAnnoElements = roundEnvironment.getElementsAnnotatedWith(ServiceAnno.class);
            Set<? extends Element> serviceDecoratorAnnoElements = roundEnvironment.getElementsAnnotatedWith(ServiceDecoratorAnno.class);
            parseServiceAnnotation(serviceAnnoElements);
            parseServiceDecoratorAnnotation(serviceDecoratorAnnoElements);
            createImpl();
            return true;
        }
        return false;
    }

    private final List<Element> serviceAnnoElementList = new ArrayList<>();
    private final Map<String, Element> serviceDecoratorAnnoElementList = new HashMap<>();

    private void parseServiceAnnotation(Set<? extends Element> annoElements) {
        serviceAnnoElementList.clear();
        for (Element element : annoElements) {
            // 如果是一个 Service
            final ServiceAnno anno = element.getAnnotation(ServiceAnno.class);
            if (anno == null) {
                continue;
            }
            List<String> classNames = getInterServiceClassNames(anno);
            if (anno.name().length != 0) { // 如果 name 填写了, 那么久必须和 class 数组一致
                if (anno.name().length != classNames.size()) {
                    throw new RuntimeException(element.getSimpleName() + " @ServiceAnno: The length of name[] must equal to the length of value[] when length > 0");
                }
            }
            serviceAnnoElementList.add(element);
        }
    }

    private void parseServiceDecoratorAnnotation(Set<? extends Element> annoElements) {
        serviceDecoratorAnnoElementList.clear();
        for (Element element : annoElements) {
            // 如果是一个 Service
            final ServiceDecoratorAnno anno = element.getAnnotation(ServiceDecoratorAnno.class);
            if (anno == null) {
                continue;
            }
            serviceDecoratorAnnoElementList.put(UUID.randomUUID().toString(), element);
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
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onCreate")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addParameter(parameterSpec)
                .addModifiers(Modifier.PUBLIC);
        methodSpecBuilder.addStatement("super.onCreate(application)");
        for (final Map.Entry<String, Element> entry : serviceDecoratorAnnoElementList.entrySet()) {
            String serviceImplClassName = entry.getValue().toString();
            TypeMirror serviceDecoratorImplTypeMirror = mElements.getTypeElement(serviceImplClassName).asType();
            final TypeName serviceDecoratorImplTypeName = TypeName.get(serviceDecoratorImplTypeMirror);
            final ServiceDecoratorAnno anno = entry.getValue().getAnnotation(ServiceDecoratorAnno.class);
            final ConditionalAnno conditionalAnno = entry.getValue().getAnnotation(ConditionalAnno.class);
            // 添加条件的 if
            boolean isHaveConditional = false;
            if (conditionalAnno != null) {
                StringBuffer conditionsSB = new StringBuffer();
                List<String> conditionsImplClassNames = getConditionsImplClassName(conditionalAnno);
                List<Object> conditionsArgs = new ArrayList<>(2 * conditionsImplClassNames.size());
                Utils.generateCondition(mElements, mConditionCacheTypeElement, conditionsSB, conditionsArgs, conditionsImplClassNames);
                methodSpecBuilder.beginControlFlow("if(" + conditionsSB.toString() + ")", conditionsArgs.toArray());
                isHaveConditional = true;
            }
            final String implName = "implName" + atomicInteger.incrementAndGet();
            String implClassStr = getInterServiceClassNames(anno);
            ClassName decoratorInterfaceClassName = ClassName.get(mElements.getTypeElement(implClassStr));
            String serviceDecoratorName = "serviceDecorator" + atomicInteger.incrementAndGet();
            MethodSpec.Builder priorityMethodBuilder = MethodSpec.methodBuilder("priority")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            priorityMethodBuilder
                    .addStatement("return $L", anno.priority())
                    .returns(int.class);
            MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")
                    .addParameter(decoratorInterfaceClassName, "target")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            getMethodBuilder
                    .addStatement("$T $N = new $T(target)", serviceDecoratorImplTypeName, serviceDecoratorName, serviceDecoratorImplTypeName);
            getMethodBuilder
                    .addStatement("return $N", serviceDecoratorName)
                    .returns(decoratorInterfaceClassName);
            TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(decoratorCallableClassName, decoratorInterfaceClassName))
                    .addMethod(getMethodBuilder.build())
                    .addMethod(priorityMethodBuilder.build())
                    .build();
            methodSpecBuilder.addStatement("$T $N = $L", decoratorCallableClassName, implName, innerTypeSpec);
            methodSpecBuilder.addStatement(
                    "$T.registerDecorator($T.class, $S, $N)",
                    classNameServiceContainer, decoratorInterfaceClassName, entry.getKey(), implName
            );
            // 条件的结束
            if (isHaveConditional) {
                methodSpecBuilder.endControlFlow();
            }
        }
        serviceAnnoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                String serviceImplCallPath = null;
                TypeMirror serviceImplTypeMirror = null;
                TypeName serviceImplTypeName = null;
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
                getOrRawMethodBuilder
                        .addStatement("return $N", serviceName)
                        .returns(serviceImplTypeName);
                TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(anno.singleTon() ? singletonLazyLoadClassName : lazyLoadClassName, serviceImplTypeName))
                        .addMethod(getOrRawMethodBuilder.build())
                        .build();
                methodSpecBuilder.addStatement("$T $N = $L", lazyLoadClassName, implName, innerTypeSpec);
                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                boolean isUseOne = anno.name().length == 0;
                for (int i = 0; i < interServiceClassNames.size(); i++) {
                    String interServiceClassName = interServiceClassNames.get(i);
                    ClassName implClassName = ClassName.get(mElements.getTypeElement(interServiceClassName));
                    if (anno.autoInit()) {
                        methodSpecBuilder.addStatement(
                                "$T.registerAutoInit($T.class)",
                                classNameServiceContainer, implClassName
                        );
                    }
                    String name = isUseOne ? "" : anno.name()[i];
                    methodSpecBuilder.addStatement(
                            "$T.register($T.class, $S, $N)",
                            classNameServiceContainer, implClassName, name, implName
                    );
                }
            }
        });

        return methodSpecBuilder.build();
    }

    private MethodSpec generateOnDestroyMethod() {
        TypeName returnType = TypeName.VOID;
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onDestroy")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        methodSpecBuilder.addStatement("super.onDestroy()");
        for (final Map.Entry<String, Element> entry : serviceDecoratorAnnoElementList.entrySet()) {
            final ServiceDecoratorAnno anno = entry.getValue().getAnnotation(ServiceDecoratorAnno.class);
            ClassName interServiceClassName = ClassName.get(mElements.getTypeElement(getInterServiceClassNames(anno)));
            methodSpecBuilder.addStatement("$T.unregisterDecorator($T.class, $S)", classNameServiceContainer, interServiceClassName, entry.getKey());
        }
        serviceAnnoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                ServiceAnno anno = element.getAnnotation(ServiceAnno.class);
                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                boolean isUseOne = anno.name().length == 0;
                for (int i = 0; i < interServiceClassNames.size(); i++) {
                    String interServiceClassNameStr = interServiceClassNames.get(i);
                    String name = isUseOne ? "" : anno.name()[i];
                    ClassName interServiceClassName = ClassName.get(mElements.getTypeElement(interServiceClassNameStr));
                    if (anno.autoInit()) {
                        methodSpecBuilder.addStatement(
                                "$T.unregisterAutoInit($T.class)",
                                classNameServiceContainer, interServiceClassName
                        );
                    }
                    methodSpecBuilder.addStatement("$T.unregister($T.class, $S)", classNameServiceContainer, interServiceClassName, name);
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
     */
    private List<String> getInterServiceClassNames(ServiceAnno anno) {
        List<String> implClassNames = new ArrayList<>();
        try {
            implClassNames.clear();
            Class[] classes = anno.value();
            for (Class clazz : classes) {
                implClassNames.add(clazz.getName());
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

    private String getInterServiceClassNames(ServiceDecoratorAnno anno) {
        try {
            return anno.value().getName();
        } catch (MirroredTypesException e) {
            List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            return typeMirrors.get(0).toString();
        }
    }

    private List<String> getConditionsImplClassName(ConditionalAnno anno) {
        List<String> implClassNames = new ArrayList<>();
        try {
            implClassNames.clear();
            //这里会报错，此时在catch中获取到拦截器的全类名
            final Class[] interceptors = anno.conditions();
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
