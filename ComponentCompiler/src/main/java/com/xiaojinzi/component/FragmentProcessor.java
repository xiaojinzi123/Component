package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.FragmentAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

/**
 * 支持 Fragment 的路由的注解驱动器
 */
@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(ComponentUtil.FRAGMENTANNO_CLASS_NAME)
public class FragmentProcessor extends BaseHostProcessor {

    private static final String NAME_OF_APPLICATION = "application";

    private ClassName classNameFragmentContainer;
    private ClassName function1ClassName;
    private ClassName singletonFunction1ClassName;
    private TypeElement bundleTypeElement;
    private TypeName bundleTypeName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        final TypeElement typeElementFragmentContainer = mElements.getTypeElement(ComponentConstants.FRAGMENT_MANAGER_CALL_CLASS_NAME);
        classNameFragmentContainer = ClassName.get(typeElementFragmentContainer);
        final TypeElement function1TypeElement = mElements.getTypeElement(ComponentConstants.FUNCTION1_CLASS_NAME);
        final TypeElement singletonFunctionTypeElement = mElements.getTypeElement(ComponentConstants.SINGLETON_FUNCTION1_CLASS_NAME);
        function1ClassName = ClassName.get(function1TypeElement);
        singletonFunction1ClassName = ClassName.get(singletonFunctionTypeElement);
        bundleTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_BUNDLE);
        bundleTypeName = TypeName.get(bundleTypeElement.asType());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (componentHost == null || componentHost.isEmpty()) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> annoElements = roundEnvironment.getElementsAnnotatedWith(FragmentAnno.class);
            parseAnnotation(annoElements);
            createImpl();
            return true;
        }
        return false;
    }

    private final List<Element> annoElementList = new ArrayList<>();

    private final Set<String> nameSet = new HashSet<>();

    private void parseAnnotation(Set<? extends Element> annoElements) {
        annoElementList.clear();
        nameSet.clear();
        for (Element element : annoElements) {
            // 如果是一个 Service
            final FragmentAnno anno = element.getAnnotation(FragmentAnno.class);
            if (anno == null) {
                continue;
            }
            String[] fragmentNames = anno.value();
            for (String fragmentName : fragmentNames) {
                if ("".equalsIgnoreCase(fragmentName)) {
                    throw new ProcessException("the name of fragment can't be empty or null");
                }
                // 一个模块不能有相同的名称, 如果模块之间有相同的只能 debug 的时候开启 check 检查
                if (nameSet.contains(fragmentName)) {
                    throw new ProcessException("the name of '" + fragmentName + "' is already exist");
                }
                nameSet.add(fragmentName);
            }
            annoElementList.add(element);
        }
    }

    private void createImpl() {

        String claName = ComponentUtil.genHostFragmentClassName(componentHost);
        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.FRAGMENT_IMPL_CLASS_NAME));
        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec onCreateMethod = generateOnCreateMethod();
        MethodSpec onDestroyMethod = generateOnDestroyMethod();
        MethodSpec onGetFragmentMapMethod = generateGetFragmentMapMethod();
        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                .addAnnotation(mClassNameKeep)
                .addAnnotation(mClassNameComponentGeneratedAnno)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(onCreateMethod)
                .addMethod(onDestroyMethod)
                .addMethod(onGetFragmentMapMethod)
                .build();

        try {
            JavaFile.builder(pkg, typeSpec
            ).indent("    ").build().writeTo(mFiler);
        } catch (IOException ignore) {
            // ignore
        }
    }

    private MethodSpec generateOnCreateMethod() {

        final ParameterSpec bundleParameter = ParameterSpec.builder(bundleTypeName, "bundle").build();

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
        final AtomicInteger atomicInteger = new AtomicInteger();
        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                String serviceImplCallPath = null;
                TypeName serviceImplTypeName = null;
                if (element instanceof ExecutableElement) {
                    // 注解在方法上了
                    ExecutableElement methodElement = (ExecutableElement) element;
                    serviceImplTypeName = TypeName.get(methodElement.getReturnType());
                    // 获取声明这个方法的类的 TypeElement
                    TypeElement declareClassType = (TypeElement) methodElement.getEnclosingElement();
                    // 调用这个静态方法的全路径
                    serviceImplCallPath = declareClassType.toString() + "." + methodElement.getSimpleName();
                } else {
                    String serviceImplClassName = element.toString();
                    serviceImplTypeName = TypeName.get(mElements.getTypeElement(serviceImplClassName).asType());
                }
                FragmentAnno anno = element.getAnnotation(FragmentAnno.class);
                String implName = "implName" + atomicInteger.incrementAndGet();

                MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("apply")
                        .addAnnotation(Override.class)
                        .addParameter(bundleParameter)
                        .addModifiers(Modifier.PUBLIC);
                if (serviceImplCallPath == null) {
                    boolean haveDefaultConstructor = isHaveDefaultConstructor(element.toString());
                    getMethodBuilder
                            .beginControlFlow("if(bundle == null)")
                            .addStatement("bundle = new Bundle()")
                            .endControlFlow()
                            .addStatement("$T fragment =  new $T($N)", serviceImplTypeName, serviceImplTypeName, (haveDefaultConstructor ? "" : NAME_OF_APPLICATION))
                            .addStatement("fragment.setArguments(bundle)")
                            .addStatement("return fragment")
                            .returns(TypeName.get(element.asType()));
                } else {
                    getMethodBuilder
                            .beginControlFlow("if(bundle == null)")
                            .addStatement("bundle = new Bundle()")
                            .endControlFlow()
                            .addStatement("return $N(bundle)", serviceImplCallPath)
                            .returns(serviceImplTypeName);
                }
                TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(function1ClassName, bundleTypeName, serviceImplTypeName))
                        .addMethod(getMethodBuilder.build())
                        .build();
                methodSpecBuilder.addStatement("$T $N = $L", function1ClassName, implName, innerTypeSpec);

                List<String> fragmentFlags = Arrays.asList(anno.value());
                for (String fragmentFlag : fragmentFlags) {
                    methodSpecBuilder.addStatement("$T.register($S,$N)", classNameFragmentContainer, fragmentFlag, implName);
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
        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                FragmentAnno anno = element.getAnnotation(FragmentAnno.class);
                List<String> fragmentFlags = Arrays.asList(anno.value());
                for (String fragmentFlag : fragmentFlags) {
                    methodSpecBuilder.addStatement("$T.unregister($S)", classNameFragmentContainer, fragmentFlag);
                }
            }
        });
        return methodSpecBuilder.build();
    }

    private MethodSpec generateGetFragmentMapMethod() {

        ParameterizedTypeName fragmentMapParameterizedTypeName =
                ParameterizedTypeName.get(mClassNameHashSet, TypeName.get(mTypeElementString.asType()));

        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("getFragmentNameSet")
                .returns(fragmentMapParameterizedTypeName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        methodSpecBuilder.addStatement("HashSet<String> set = new HashSet();");
        annoElementList.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                FragmentAnno anno = element.getAnnotation(FragmentAnno.class);
                List<String> fragmentFlags = Arrays.asList(anno.value());
                for (String fragmentFlag : fragmentFlags) {
                    methodSpecBuilder.addStatement("set.add($S)", fragmentFlag);
                }
            }
        });
        methodSpecBuilder.addStatement("return set");
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
