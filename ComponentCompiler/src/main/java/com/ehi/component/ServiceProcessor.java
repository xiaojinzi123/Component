package com.ehi.component;

import com.ehi.component.anno.EHiServiceAnno;
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
@SupportedAnnotationTypes({"com.ehi.component.anno.EHiServiceAnno"})
public class ServiceProcessor extends AbstractProcessor {

    private static final String SERVICE = "com.ehi.component.service.EHiService";

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
                EHiServiceAnno anno = element.getAnnotation(EHiServiceAnno.class);
                boolean haveDefaultConstructor = isHaveDefaultConstructor(element.toString());
                String implName = "implName" + atomicInteger.incrementAndGet();

//                SingletonService<Component1ServiceImpl> implName1 = new SingletonService<Component1ServiceImpl>() {
//                    @Override
//                    protected Component1ServiceImpl getRaw() {
//                        return new Component1ServiceImpl(application);
//                    }
//                };

                if (anno.singleTon()) {
                    methodSpecBuilder.addStatement("com.ehi.component.service.SingletonService<$N> $N = new com.ehi.component.service.SingletonService<$N>() {" +
                            "\n\t@Override" +
                            "\n\tprotected $N getRaw() {" +
                            "\n\t\treturn new $N($N);" +
                            "\n\t}" +
                            "}", serviceImplClassName, implName, serviceImplClassName, serviceImplClassName, serviceImplClassName, (haveDefaultConstructor ? "" : "application")
                    );
                } else {
                    methodSpecBuilder.addStatement("com.ehi.component.service.IServiceLoad<$N> $N = new com.ehi.component.service.IServiceLoad<$N>() {" +
                            "\n\t@Override" +
                            "\n\tpublic $N get() {" +
                            "\n\t\treturn new $N($N);" +
                            "\n\t}" +
                            "}", serviceImplClassName, implName, serviceImplClassName, serviceImplClassName, serviceImplClassName, (haveDefaultConstructor ? "" : "application")
                    );
                }

                List<String> interServiceClassNames = getInterServiceClassNames(anno);
                for (String interServiceClassName : interServiceClassNames) {
                    methodSpecBuilder.addStatement("$N.register($N.class,$N)", SERVICE, interServiceClassName, implName);
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
                    methodSpecBuilder.addStatement("$N.unregister($N.class)", SERVICE, interServiceClassName);
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
