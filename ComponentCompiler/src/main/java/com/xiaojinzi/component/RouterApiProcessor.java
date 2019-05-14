package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * 负责把像 Retrofit 那样的接口生成对应的实现类
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.ROUTERAPIANNO_CLASS_NAME})
public class RouterApiProcessor extends BaseProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            final Set<? extends Element> routeApiElements = roundEnvironment.getElementsAnnotatedWith(RouterApiAnno.class);
            parseAnno(routeApiElements);
            createRouterApiImpls();
            return true;
        }
        return false;
    }

    private Set<TypeElement> mRouteApiElements = new HashSet<>();

    private void parseAnno(Set<? extends Element> routeApiElements) {
        for (Element apiElement : routeApiElements) {
            if (apiElement instanceof TypeElement) {
                mRouteApiElements.add((TypeElement) apiElement);
            }
        }
    }

    private void createRouterApiImpls() {
        for (TypeElement routeApiElement : mRouteApiElements) {
            createRouterApiImpl(routeApiElement);
        }
    }

    /**
     * 生成一个 RouterApi 的实现类
     *
     * @param typeElement
     */
    private void createRouterApiImpl(TypeElement typeElement) {
        // 整个类默认的host注解
        HostAnno hostAnnotation = typeElement.getAnnotation(HostAnno.class);
        // 拿到全类型
        final String claName = typeElement.getQualifiedName().toString() + ComponentUtil.UIROUTERAPI;
        //pkg
        final String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        final String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        final ClassName superClass = ClassName.get(typeElement);
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(cn)
                //.addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addSuperinterface(superClass);

        implementInterfaceMethods(typeSpecBuilder, typeElement);

        try {
            JavaFile.builder(pkg, typeSpecBuilder.build())
                    .indent("    ")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }

    /**
     * 实现接口中所有的方法
     *
     * @param typeSpecBuilder
     */
    private void implementInterfaceMethods(TypeSpec.Builder typeSpecBuilder, TypeElement apiTypeElement) {

        // 拿出所有的方法
        List<? extends Element> enclosedElements = apiTypeElement.getEnclosedElements();
        for (Element methodElement : enclosedElements) {
            if (methodElement instanceof ExecutableElement) {
                implementInterfaceMethod(typeSpecBuilder, (ExecutableElement) methodElement);
            }
        }

    }

    private void implementInterfaceMethod(TypeSpec.Builder typeSpecBuilder, ExecutableElement executableElement) {

        HostAnno hostAnnotation = executableElement.getAnnotation(HostAnno.class);
        PathAnno pathAnnotation = executableElement.getAnnotation(PathAnno.class);
        HostAndPathAnno hostAndPathAnnotation = executableElement.getAnnotation(HostAndPathAnno.class);

        // 返回的返回对象
        TypeMirror returnType = executableElement.getReturnType();
        // 方法名
        Name methodName = executableElement.getSimpleName();
        // 所有的方法的方法
        List<? extends VariableElement> parameters = executableElement.getParameters();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName.toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(returnType));

        for (VariableElement parameter : parameters) {
            methodBuilder.addParameter(ParameterSpec
                    .builder(
                            TypeName.get(parameter.asType()),
                            parameter.getSimpleName().toString()
                    )
                    .build()
            );
        }

        typeSpecBuilder.addMethod(methodBuilder.build());

    }

}
