package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.FiledAutowiredAnno;
import com.xiaojinzi.component.anno.ServiceAutowiredAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

/**
 * 针对注入的两个注解起作用
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.FILEDAUTOWIREDANNO_CLASS_NAME, ComponentUtil.SERVICEAUTOWIREDANNO_CLASS_NAME})
public class AutowireProcessor extends BaseProcessor {

    private TypeElement charsequenceTypeElement;
    private TypeMirror charsequenceTypeMirror;
    private TypeName charsequenceTypeName;

    private TypeName injectTypeName = null;
    private ClassName injectClassName = null;

    private TypeMirror parameterSupportTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;
    private TypeMirror activityTypeMirror;
    private TypeMirror fragmentTypeMirror;

    private TypeElement serviceTypeElement;
    private ClassName serviceClassName;

    private TypeElement bundleTypeElement;

    private ParameterizedTypeName stringArrayListParameterizedTypeName;
    private ParameterizedTypeName integerArrayListParameterizedTypeName;
    private ParameterizedTypeName parcelableArrayListParameterizedTypeName;
    private ParameterizedTypeName charsequenceArrayListParameterizedTypeName;

    private AtomicInteger intCount = new AtomicInteger();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        bundleTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_BUNDLE);

        charsequenceTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_CHARSEQUENCE);
        charsequenceTypeMirror = charsequenceTypeElement.asType();
        charsequenceTypeName = ClassName.get(charsequenceTypeMirror);

        serviceTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.SERVICE_CLASS_NAME);
        serviceClassName = ClassName.get(serviceTypeElement);
        TypeElement injectTypeElement = mElements.getTypeElement(ComponentConstants.INJECT_CLASS_NAME);
        injectTypeName = TypeName.get(injectTypeElement.asType());
        injectClassName = ClassName.get(injectTypeElement);
        final TypeElement parameterSupportTypeElement = mElements.getTypeElement(ComponentConstants.PARAMETERSUPPORT_CLASS_NAME);
        parameterSupportTypeMirror = parameterSupportTypeElement.asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();

        stringArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, mClassNameString);
        integerArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, ClassName.INT.box());
        parcelableArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, TypeName.get(parcelableTypeMirror));
        charsequenceArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, TypeName.get(charsequenceTypeMirror));

        activityTypeMirror = mElements.getTypeElement(ComponentConstants.ANDROID_ACTIVITY).asType();
        fragmentTypeMirror = mElements.getTypeElement(ComponentConstants.ANDROID_V4_FRAGMENT).asType();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            final Set<? extends Element> filedAutowiredElements = roundEnvironment.getElementsAnnotatedWith(FiledAutowiredAnno.class);
            final Set<? extends Element> serviceAutowiredElements = roundEnvironment.getElementsAnnotatedWith(ServiceAutowiredAnno.class);
            final Set<Element> annotatedElements = new HashSet<>();
            annotatedElements.addAll(filedAutowiredElements);
            annotatedElements.addAll(serviceAutowiredElements);
            final Set<VariableElement> fieldElements = new HashSet<>();
            for (Element element : annotatedElements) {
                // 必须标记的是一个类的字段,这个才会被处理
                if (element instanceof VariableElement && element.getEnclosingElement() instanceof TypeElement) {
                    VariableElement variableElement = (VariableElement) element;
                    fieldElements.add(variableElement);
                }
            }
            // 找到标记到相同的一个类上的所有注解
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
                .addSuperinterface(ParameterizedTypeName.get(injectClassName, TypeName.get(mElements.getTypeElement(fullClassName).asType())))
                .addMethod(injectMethodForView(targetClass))
                .addMethod(injectMethod(targetClass, parameterFieldSet))
                .addAnnotation(mClassNameKeep);
        try {
            JavaFile.builder(pkg, classBuilder.build()
            ).indent("    ").build().writeTo(mFiler);
        } catch (IOException ignore) {
            // ignore
        }
    }

    private MethodSpec injectMethodForView(TypeElement targetClass) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("inject")
                .addJavadoc("属性注入\n")
                .addParameter(
                        ParameterSpec
                                .builder(TypeName.get(targetClass.asType()), "target")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC);
        // 如果是 Activity
        if (mTypes.isSubtype(targetClass.asType(), activityTypeMirror)) {
            methodBuilder.addStatement("inject(target, target.getIntent().getExtras())");
        } else if (mTypes.isSubtype(targetClass.asType(), fragmentTypeMirror)) {
            methodBuilder.addStatement("inject(target, target.getArguments())");
        }
        return methodBuilder.build();
    }

    private MethodSpec injectMethod(TypeElement targetClass, Set<VariableElement> parameterFieldSet) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("inject")
                .addJavadoc("属性注入\n")
                .addParameter(
                        ParameterSpec
                                .builder(TypeName.get(targetClass.asType()), "target")
                                .build()
                )
                .addParameter(
                        ParameterSpec
                                .builder(TypeName.get(bundleTypeElement.asType()), "bundle")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC);
        boolean isAutowireParameter = true;
        // 如果是 Activity
        if (mTypes.isSubtype(targetClass.asType(), activityTypeMirror)) {
        } else if (mTypes.isSubtype(targetClass.asType(), fragmentTypeMirror)) {
        } else {
            isAutowireParameter = false;
        }
        for (VariableElement variableElement : parameterFieldSet) {
            // 生成一个不重复的参数名字
            String parameterName = "target." + variableElement.getSimpleName();
            generateParameterCodeForInject(
                    isAutowireParameter,
                    variableElement, methodBuilder,
                    parameterName, "bundle"
            );
        }
        return methodBuilder.build();
    }

    public void generateParameterCodeForInject(boolean isAutowireParameter,
                                               VariableElement variableElement,
                                               MethodSpec.Builder methodBuilder,
                                               String parameterName,
                                               String bundleCallStr) {
        ServiceAutowiredAnno serviceAutowiredAnno = variableElement.getAnnotation(ServiceAutowiredAnno.class);
        FiledAutowiredAnno filedAutowiredAnno = variableElement.getAnnotation(FiledAutowiredAnno.class);

        TypeMirror variableTypeMirror = variableElement.asType();
        TypeName parameterTypeName = ClassName.get(variableTypeMirror);

        if (isAutowireParameter && filedAutowiredAnno != null) {
            if (parameterTypeName.equals(mClassNameString)) { // 如果是一个 String
                methodBuilder.addStatement("$N = $T.getString($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(charsequenceTypeName)) { // 如果是一个 charsequence
                methodBuilder.addStatement("$N = $T.getCharSequence($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.CHAR) || parameterTypeName.equals(ClassName.CHAR.box())) { // 如果是一个 char or Char
                methodBuilder.addStatement("$N = $T.getChar($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.BYTE) || parameterTypeName.equals(ClassName.BYTE.box())) { // 如果是一个byte or Byte
                methodBuilder.addStatement("$N = $T.getByte($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.SHORT) || parameterTypeName.equals(ClassName.SHORT.box())) { // 如果是一个short or Short
                methodBuilder.addStatement("$N = $T.getShort($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.INT) || parameterTypeName.equals(ClassName.INT.box())) { // 如果是一个int or Integer
                methodBuilder.addStatement("$N = $T.getInt($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.LONG) || parameterTypeName.equals(ClassName.LONG.box())) { // 如果是一个long or Long
                methodBuilder.addStatement("$N = $T.getLong($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.FLOAT) || parameterTypeName.equals(ClassName.FLOAT.box())) { // 如果是一个float or Float
                methodBuilder.addStatement("$N = $T.getFloat($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.DOUBLE) || parameterTypeName.equals(ClassName.DOUBLE.box())) { // 如果是一个double or Double
                methodBuilder.addStatement("$N = $T.getDouble($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parameterTypeName.equals(ClassName.BOOLEAN) || parameterTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是一个boolean or Boolean
                methodBuilder.addStatement("$N = $T.getBoolean($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (stringArrayListParameterizedTypeName.equals(TypeName.get(variableTypeMirror))) {
                methodBuilder.addStatement("$N = $T.getStringArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (integerArrayListParameterizedTypeName.equals(TypeName.get(variableTypeMirror))) {
                methodBuilder.addStatement("$N = $T.getIntegerArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (parcelableArrayListParameterizedTypeName.equals(TypeName.get(variableTypeMirror))) {
                methodBuilder.addStatement("$N = $T.getParcelableArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (charsequenceArrayListParameterizedTypeName.equals(TypeName.get(variableTypeMirror))) {
                methodBuilder.addStatement("$N = $T.getCharSequenceArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
            } else if (variableTypeMirror instanceof ArrayType) {
                ArrayType parameterArrayType = (ArrayType) variableTypeMirror;
                TypeName parameterComponentTypeName = ClassName.get(parameterArrayType.getComponentType());
                // 如果是一个 String[]
                if (parameterArrayType.getComponentType().equals(mTypeElementString.asType())) {
                    methodBuilder.addStatement("$N = $T.getStringArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterArrayType.getComponentType().equals(charsequenceTypeElement.asType())) {
                    methodBuilder.addStatement("$N = $T.getCharSequenceArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterArrayType.getComponentType().equals(mTypeElementString.asType())) {
                    methodBuilder.addStatement("$N = $T.getStringArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.BYTE) || parameterComponentTypeName.equals(ClassName.BYTE.box())) { // 如果是 byte
                    methodBuilder.addStatement("$N = $T.getByteArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.CHAR) || parameterComponentTypeName.equals(ClassName.CHAR.box())) { // 如果是 char
                    methodBuilder.addStatement("$N = $T.getCharArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.SHORT) || parameterComponentTypeName.equals(ClassName.SHORT.box())) { // 如果是 short
                    methodBuilder.addStatement("$N = $T.getShortArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.INT) || parameterComponentTypeName.equals(ClassName.INT.box())) { // 如果是 int
                    methodBuilder.addStatement("$N = $T.getIntArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.LONG) || parameterComponentTypeName.equals(ClassName.LONG.box())) { // 如果是 long
                    methodBuilder.addStatement("$N = $T.getLongArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.FLOAT) || parameterComponentTypeName.equals(ClassName.FLOAT.box())) { // 如果是 float
                    methodBuilder.addStatement("$N = $T.getFloatArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.DOUBLE) || parameterComponentTypeName.equals(ClassName.DOUBLE.box())) { // 如果是 double
                    methodBuilder.addStatement("$N = $T.getDoubleArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (parameterComponentTypeName.equals(ClassName.BOOLEAN) || parameterComponentTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是 boolean
                    methodBuilder.addStatement("$N = $T.getBooleanArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else if (mTypes.isSubtype(parameterArrayType.getComponentType(), parcelableTypeMirror)) {  // 如果是 Parcelable
                    methodBuilder.addStatement("$N = $T.getParcelableArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, filedAutowiredAnno.value(), parameterName);
                } else {
                    throw new ProcessException("can't to resolve unknow type parameter(" + variableElement.getEnclosingElement().getSimpleName() + "#" + variableElement.getSimpleName().toString() + ")");
                }
            } else { // 其他类型的情况,是实现序列化的对象,这种时候我们直接要从 bundle 中获取
                String isHaveValueName = "isHaveValue" + intCount.incrementAndGet();
                methodBuilder.addStatement("boolean $N = false", isHaveValueName);
                // 优先获取 parcelable
                if (mTypes.isSubtype(variableTypeMirror, parcelableTypeMirror)) {
                    methodBuilder.beginControlFlow("if ($N.containsKey($S) && $N.getParcelable($S) != null)", bundleCallStr, filedAutowiredAnno.value(), bundleCallStr, filedAutowiredAnno.value());
                    methodBuilder.addStatement("$N = ($T) $N.getParcelable($S)", parameterName, variableTypeMirror, bundleCallStr, filedAutowiredAnno.value());
                    methodBuilder.addStatement("$N = true", isHaveValueName);
                    methodBuilder.endControlFlow();
                }
                if (mTypes.isSubtype(variableTypeMirror, serializableTypeMirror)) {
                    methodBuilder.beginControlFlow("if (!$N && $N.containsKey($S) && $N.getSerializable($S) != null) ", isHaveValueName, bundleCallStr, filedAutowiredAnno.value(), bundleCallStr, filedAutowiredAnno.value());
                    methodBuilder.addStatement("$N = ($T) $N.getSerializable($S)", parameterName, variableTypeMirror, bundleCallStr, filedAutowiredAnno.value());
                    methodBuilder.endControlFlow();
                }
            }
        } else if (serviceAutowiredAnno != null) {
            methodBuilder.addComment("may be null here");
            methodBuilder.addStatement("$N = $T.get($T.class)", parameterName, serviceTypeElement, parameterTypeName);
        }

    }

}
