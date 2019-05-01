package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.ParameterAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.PARAMETERANNO_CLASS_NAME})
public class ParameterInjectProcessor extends BaseProcessor {

    private TypeName parameterInjectTypeName = null;
    private ClassName parameterInjectClassName = null;

    private TypeMirror parameterSupportTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        TypeElement parameterInjectTypeElement = mElements.getTypeElement(ComponentConstants.PARAMETERINJECT_CLASS_NAME);
        parameterInjectTypeName = TypeName.get(parameterInjectTypeElement.asType());
        parameterInjectClassName = ClassName.get(parameterInjectTypeElement);
        final TypeElement parameterSupportTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.PARAMETERSUPPORT_CLASS_NAME);
        parameterSupportTypeMirror = parameterSupportTypeElement.asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            final Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(ParameterAnno.class);
            final Set<VariableElement> fieldElements = new HashSet<>();
            for (Element element : annotatedElements) {
                if (element instanceof VariableElement && element.getEnclosingElement() instanceof TypeElement) {
                    VariableElement variableElement = (VariableElement) element;
                    fieldElements.add(variableElement);
                }
            }
            mMessager.printMessage(Diagnostic.Kind.NOTE, "fieldElements = " + fieldElements.toString());
            findSameTargetElement(fieldElements);
            createImpl();
            return true;
        }
        return false;
    }

    /**
     * 这里面存的是每一个类针对的所有标记的字段
     */
    private Map<TypeElement, Set<VariableElement>> map = new HashMap<>();

    private void findSameTargetElement(final Set<VariableElement> fieldElements) {
        for (VariableElement variableElement : fieldElements) {
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            Set<VariableElement> variableElements = map.get(typeElement);
            if (variableElements == null) {
                variableElements = new HashSet<>();
                map.put(typeElement, variableElements);
            }
            variableElements.add(variableElement);
        }
    }

    private void createImpl() {
        Set<Map.Entry<TypeElement, Set<VariableElement>>> entrySet = map.entrySet();
        for (Map.Entry<TypeElement, Set<VariableElement>> entry : entrySet) {
            // 标记注解的类
            TypeElement targetClass = entry.getKey();
            // 这个类标记了注解的字段集合
            Set<VariableElement> parameterFieldSet = entry.getValue();
            createInjectClass(targetClass, parameterFieldSet);
        }
    }

    /**
     * 创建某个类的Inject实现
     *
     * @param targetClass
     * @param parameterFieldSet
     */
    private void createInjectClass(TypeElement targetClass, Set<VariableElement> parameterFieldSet) {
        // 拿到注解类的全类名
        String fullClassName = targetClass.getQualifiedName().toString();
        int lastPointIndex = fullClassName.lastIndexOf('.');
        //pkg
        String pkg = fullClassName.substring(0, lastPointIndex);
        //simpleName
        String className = fullClassName.substring(lastPointIndex + 1);
        TypeSpec.Builder classBuilder = TypeSpec
                .classBuilder(className + ComponentConstants.INJECT_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(parameterInjectClassName, TypeName.get(mElements.getTypeElement(fullClassName).asType())))
                .addMethod(injectMethod(targetClass, parameterFieldSet));
        try {
            JavaFile.builder(pkg, classBuilder.build()
            ).indent("    ").build().writeTo(mFiler);
        } catch (IOException ignore) {
            // ignore
        }
    }

    private MethodSpec injectMethod(TypeElement targetClass, Set<VariableElement> parameterFieldSet) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("inject")
                .addJavadoc("属性注入")
                .addParameter(
                        ParameterSpec
                                .builder(TypeName.get(targetClass.asType()), "target")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC);
        methodBuilder.addStatement("android.os.Bundle bundle = target.getIntent().getExtras()");
        for (VariableElement variableElement : parameterFieldSet) {
            // 生成一个不重复的参数名字
            String parameterName = "target." + variableElement.getSimpleName();
            Utils.generateParameterCodeForInject(
                    mTypes, variableElement, methodBuilder,
                    parameterSupportTypeMirror, parameterName,"bundle",
                    mClassNameString, serializableTypeMirror, parcelableTypeMirror
            );
        }
        return methodBuilder.build();
    }

}
