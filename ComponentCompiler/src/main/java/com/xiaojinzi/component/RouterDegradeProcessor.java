package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.RouterDegradeAnno;
import com.xiaojinzi.component.bean.RouterDegradeAnnoBean;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

@AutoService(Processor.class)
@SupportedOptions("HOST")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ComponentUtil.ROUTER_DEGRADE_ANNO_CLASS_NAME})
public class RouterDegradeProcessor extends BaseHostProcessor {

    private TypeMirror typeRouterDegradeTypeMirror;
    private TypeElement routerDegradeBeanTypeElement;
    private TypeElement routerInterceptorTypeElement;

    final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 降级的接口
        typeRouterDegradeTypeMirror = mElements.getTypeElement(ComponentConstants.ROUTERDEGRADE_CLASS_NAME).asType();
        routerDegradeBeanTypeElement = mElements.getTypeElement(ComponentConstants.ROUTER_DEGRADE_BEAN_CLASS_NAME);
        routerInterceptorTypeElement = mElements.getTypeElement(ComponentConstants.INTERCEPTOR_INTERFACE_CLASS_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (CollectionUtils.isNotEmpty(set)) {
            final Set<? extends Element> routerDegradeElements = roundEnvironment.getElementsAnnotatedWith(RouterDegradeAnno.class);
            parseRouterDegradeAnnotation(routerDegradeElements);
            createImpl();
            return true;
        }
        return false;
    }

    private List<RouterDegradeAnnoBean> routerDegradeAnnoBeanList = new ArrayList<>();

    private void parseRouterDegradeAnnotation(Set<? extends Element> routerDegradeElements) {
        routerDegradeAnnoBeanList.clear();
        if (routerDegradeElements == null || routerDegradeElements.size() == 0) {
            return;
        }
        for (Element element : routerDegradeElements) {

            final RouterDegradeAnno anno = element.getAnnotation(RouterDegradeAnno.class);
            if (anno == null) {
                continue;
            }
            final TypeMirror tm = element.asType();
            final boolean type = !(element instanceof TypeElement);
            final boolean subType = !mTypes.isSubtype(tm, typeRouterDegradeTypeMirror);
            // 如果标记的不是类或者不是 RouterDegrade 的子类
            if (type || subType) {
                continue;
            }

            RouterDegradeAnnoBean targetBean = new RouterDegradeAnnoBean();
            targetBean.setRawType(element);
            targetBean.setPriority(anno.priority());
            routerDegradeAnnoBeanList.add(targetBean);

        }
    }

    private void createImpl() {
        String claName = ComponentUtil.genHostRouterDegradeClassName(componentModuleName);
        //pkg
        String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        ClassName superClass = ClassName.get(mElements.getTypeElement(ComponentUtil.UIROUTER_DEGRADE_IMPL_CLASS_NAME));
        MethodSpec initHostMethod = generateInitHostMethod();
        MethodSpec initListMethod = generateInitListMethod();
        TypeSpec typeSpec = TypeSpec.classBuilder(cn)
                .addAnnotation(mClassNameKeep)
                .addAnnotation(mClassNameComponentGeneratedAnno)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addJavadoc(componentModuleName + "路由降级的模块\n")
                .superclass(superClass)
                .addMethod(initHostMethod)
                .addMethod(initListMethod)
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

    private MethodSpec generateInitHostMethod() {

        TypeName returnType = mClassNameString;
        MethodSpec.Builder openUriMethodSpecBuilder = MethodSpec.methodBuilder("getHost")
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        openUriMethodSpecBuilder.addStatement("return $S", componentModuleName);
        return openUriMethodSpecBuilder.build();

    }

    private MethodSpec generateInitListMethod() {
        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("initList")
                .returns(TypeName.VOID)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        for (RouterDegradeAnnoBean item : routerDegradeAnnoBeanList) {
            String className = item.getRawType().toString();
            methodSpecBuilder.addComment("--------------------------" + className + "--------------------------");
            // 生成变量的名字,每一个变量代表每一个目标界面的配置对象
            String routerBeanName = "routerDegradeBean" + atomicInteger.incrementAndGet();
            // 创建对象
            methodSpecBuilder.addStatement("$T " + routerBeanName + " = new $T()", routerDegradeBeanTypeElement, routerDegradeBeanTypeElement);
            // 设置优先级
            methodSpecBuilder.addStatement(routerBeanName + ".setPriority($L)", item.getPriority());
            // 设置目标 Class
            methodSpecBuilder.addStatement(routerBeanName + ".setTargetClass($N.class)", className);
            methodSpecBuilder.addStatement("routerDegradeBeanList.add($N)", routerBeanName);
        }
        return methodSpecBuilder.build();
    }

}
