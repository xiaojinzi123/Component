package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.ModuleAppAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
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

/**
 * 负责处理 {@link ModuleAppAnno}
 */
@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({com.xiaojinzi.component.ComponentUtil.MODULE_APP_ANNO_CLASS_NAME})
public class ModuleAppProcessor extends BaseHostProcessor {

    private TypeElement centerInterceptorTypeElement;
    private TypeElement centerServiceTypeElement;
    private TypeElement centerRouterDegradeTypeElement;
    private TypeElement centerFragmentTypeElement;
    private TypeElement routerCenterTypeElement;
    private TypeElement classCacheTypeElement;
    private int priority = 0;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            String priorityStr = options.get("Priority");
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (Exception ignore) {
            }
        }
        centerInterceptorTypeElement = mElements.getTypeElement(ComponentConstants.CENTERINTERCEPTOR_CLASS_NAME);
        centerRouterDegradeTypeElement = mElements.getTypeElement(ComponentConstants.ROUTERDEGRADECENTER_CLASS_NAME);
        centerFragmentTypeElement = mElements.getTypeElement(ComponentConstants.FRAGMENT_CENTER_CALL_CLASS_NAME);
        centerServiceTypeElement = mElements.getTypeElement(ComponentConstants.CENTERSERVICE_CLASS_NAME);
        routerCenterTypeElement = mElements.getTypeElement(ComponentConstants.ROUTERCENTER_CLASS_NAME);
        classCacheTypeElement = mElements.getTypeElement(ComponentConstants.CLASSCACHE_CLASS_NAME);
        createImpl(true);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> moduleAppElements = roundEnvironment.getElementsAnnotatedWith(ModuleAppAnno.class);
            parseAnnotation(moduleAppElements);
            createImpl(false);
            return true;
        }
        return false;
    }

    private List<Element> applicationList = new ArrayList<>();

    private void parseAnnotation(Set<? extends Element> moduleAppElements) {
        applicationList.clear();
        TypeMirror typeApplicationLifecycle = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.APPLICATION_LIFECYCLE_INTERFACE_CLASS_NAME).asType();
        for (Element element : moduleAppElements) {
            TypeMirror tm = element.asType();
            if (!(element instanceof TypeElement)) {
                throw new ProcessException(element + " is not a 'TypeElement' ");
            }
            if (!mTypes.isSubtype(tm, typeApplicationLifecycle)) {
                throw new ProcessException(element + " you must implement IApplicationLifecycle interface");
            }
            // 如果是一个 Application
            ModuleAppAnno moduleApp = element.getAnnotation(ModuleAppAnno.class);
            if (moduleApp == null) {
                continue;
            }
            applicationList.add(element);
        }
    }

    private void createImpl(boolean isDefault) {

        String claName = isDefault ?
                ComponentUtil.genDefaultHostModuleApplicationClassName(componentModuleName) :
                ComponentUtil.genHostModuleApplicationClassName(componentModuleName);

        String classJavaDoc = null;
        if (isDefault) {
            classJavaDoc = "当业务组件中没有用 {@link com.xiaojinzi.component.anno.ModuleAppAnno} 注解," +
                    "\n本类就会默认被加载的生效\n";
        } else {
            classJavaDoc = "当业务组件中使用了 {@link com.xiaojinzi.component.anno.ModuleAppAnno} 注解," +
                    "\n本类就会被加载生效,默认的 {@link " + claName + "} 会失效\n";
        }

        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.MODULE_APPLICATION_IMPL_CLASS_NAME));
        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec getPriorityMethod = generateGetPriorityMethod();
        MethodSpec initMapMethod = generateInitMapMethod();
        MethodSpec onCreateMethod = generateOnCreateMethod();
        MethodSpec onDestroyMethod = generateOnDestroyMethod();
        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                .addAnnotation(mClassNameKeep)
                .addAnnotation(mClassNameComponentGeneratedAnno)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(getPriorityMethod)
                .addMethod(initMapMethod)
                .addJavadoc(classJavaDoc)
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

        /*methodSpecBuilder.addStatement("$T.getInstance().register(getHost())", centerServiceTypeElement);

        MethodSpec.Builder runnableMethodBuilder = MethodSpec
                .methodBuilder("run")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        runnableMethodBuilder.addStatement("$T.getInstance().register(getHost())", routerCenterTypeElement);
        runnableMethodBuilder.addStatement("$T.getInstance().register(getHost())", centerInterceptorTypeElement);
        runnableMethodBuilder.addStatement("$T.getInstance().register(getHost())", centerRouterDegradeTypeElement);
        runnableMethodBuilder.addStatement("$T.getInstance().register(getHost())", centerFragmentTypeElement);

        TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(Runnable.class))
                .addMethod(runnableMethodBuilder.build())
                .build();

        methodSpecBuilder.addStatement("$T $N = $L", Runnable.class, "r", innerTypeSpec);

        methodSpecBuilder.beginControlFlow("if($N.getConfig().isInitRouterAsync())", ComponentConstants.COMPONENT_CLASS_NAME);
        methodSpecBuilder.addStatement("$N.postActionToWorkThread(r)", ComponentConstants.UTILS_CLASS_NAME);
        methodSpecBuilder.endControlFlow();
        methodSpecBuilder.beginControlFlow("else");
        methodSpecBuilder.addStatement("r.run()");
        methodSpecBuilder.endControlFlow();*/

        return methodSpecBuilder.build();
    }

    private MethodSpec generateOnDestroyMethod() {
        TypeName returnType = TypeName.VOID;
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("onDestroy")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        methodSpecBuilder.addStatement("super.onDestroy()");
        /*methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", centerServiceTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", routerCenterTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", centerInterceptorTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", centerRouterDegradeTypeElement);
        methodSpecBuilder.addStatement("$T.getInstance().unregister(getHost())", centerFragmentTypeElement);
        methodSpecBuilder.addComment("清空缓存");
        methodSpecBuilder.addStatement("$T.clear()", classCacheTypeElement);*/
        return methodSpecBuilder.build();
    }

    private MethodSpec generateInitHostMethod() {
        TypeName returnType = TypeName.get(mTypeElementString.asType());
        MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("getHost")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        openUriMethodSpecBuilder.addStatement("return $S", componentModuleName);
        return openUriMethodSpecBuilder.build();
    }

    private MethodSpec generateGetPriorityMethod() {
        TypeName returnType = TypeName.get(int.class);
        MethodSpec.Builder getPriorityMethodSpecBuilder = MethodSpec.methodBuilder("getPriority")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        getPriorityMethodSpecBuilder.addStatement("return $L", priority);
        return getPriorityMethodSpecBuilder.build();
    }

}
