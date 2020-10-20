package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno;
import com.xiaojinzi.component.anno.ServiceAutowiredAnno;
import com.xiaojinzi.component.bean.ActivityAttrDocBean;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * 针对注入的两个注解起作用
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({
        ComponentUtil.ATTR_VALUE_AUTOWIREDANNO_CLASS_NAME,
        ComponentUtil.SERVICEAUTOWIREDANNO_CLASS_NAME,
})
public class AutowireProcessor extends BaseProcessor {

    private TypeElement charsequenceTypeElement;
    private TypeMirror charsequenceTypeMirror;
    private TypeName charsequenceTypeName;

    private ClassName injectClassName = null;

    private TypeMirror parameterSupportTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;
    private TypeMirror activityTypeMirror;
    private TypeMirror fragmentTypeMirror;

    private TypeElement serviceTypeElement;
    private TypeElement arrayListTypeElement;
    private ClassName arrayListClassName;

    private TypeElement bundleTypeElement;

    private AtomicInteger intCount = new AtomicInteger();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        bundleTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_BUNDLE);

        charsequenceTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_CHARSEQUENCE);
        charsequenceTypeMirror = charsequenceTypeElement.asType();
        charsequenceTypeName = ClassName.get(charsequenceTypeMirror);

        serviceTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.SERVICE_CLASS_NAME);
        TypeElement injectTypeElement = mElements.getTypeElement(ComponentConstants.INJECT_CLASS_NAME);
        injectClassName = ClassName.get(injectTypeElement);
        final TypeElement parameterSupportTypeElement = mElements.getTypeElement(ComponentConstants.PARAMETERSUPPORT_CLASS_NAME);
        parameterSupportTypeMirror = parameterSupportTypeElement.asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();

        arrayListTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_ARRAYLIST);
        arrayListClassName = ClassName.get(arrayListTypeElement);

        activityTypeMirror = mElements.getTypeElement(ComponentConstants.ANDROID_ACTIVITY).asType();
        TypeElement typeElementFragment = mElements.getTypeElement(ComponentConstants.ANDROID_FRAGMENT);
        if (typeElementFragment == null) {
            throw new ProcessException(getAddDependencyTip(Arrays.asList(ComponentConstants.ANDROID_FRAGMENT), false));
        }
        fragmentTypeMirror = typeElementFragment.asType();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            final Set<? extends Element> filedAutowiredElements = roundEnvironment.getElementsAnnotatedWith(AttrValueAutowiredAnno.class);
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
            try {
                createRouterAttrDocJson();
            } catch (IOException e) {
                // ignore
            }
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
                .addAnnotation(mClassNameKeep)
                .addAnnotation(mClassNameComponentGeneratedAnno)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(injectClassName, TypeName.get(mElements.getTypeElement(fullClassName).asType())))
                .addMethod(injectAttrValueMethod1(targetClass))
                .addMethod(injectAttrValueMethod2(targetClass, parameterFieldSet))
                .addMethod(injectServiceMethod(targetClass, parameterFieldSet));
        try {
            JavaFile.builder(pkg, classBuilder.build()
            ).indent("    ").build().writeTo(mFiler);
        } catch (IOException ignore) {
            // ignore
        }
    }

    private void createRouterAttrDocJson() throws IOException {
        if (!isRouterDocEnable()) {
            return;
        }
        File attrFolder = new File(routerDocFolder, "attr");
        if (attrFolder.exists() && attrFolder.isFile()) {
            attrFolder.delete();
        }
        attrFolder.mkdirs();

        Set<Map.Entry<TypeElement, Set<VariableElement>>> entrySet = map.entrySet();
        Gson gson = new Gson();
        // 循环, key 是目标 class, value 是 该 class 下所有待注入的字段
        for (Map.Entry<TypeElement, Set<VariableElement>> entry : entrySet) {
            List<ActivityAttrDocBean> activityAttrDocBeans = new ArrayList<>();
            TypeElement targetClass = entry.getKey();
            TypeMirror targetClassTypeMirror = targetClass.asType();
            // 如果是 Activity
            if (mTypes.isSubtype(targetClassTypeMirror, activityTypeMirror)) {
                // 所有字段
                Set<VariableElement> variableElementSet = entry.getValue();
                for (VariableElement variableElement : variableElementSet) {
                    AttrValueAutowiredAnno attrValueAutowiredAnno = variableElement.getAnnotation(AttrValueAutowiredAnno.class);
                    // 如果是注入字段
                    if (attrValueAutowiredAnno != null) {
                        ActivityAttrDocBean activityAttrDocBean = new ActivityAttrDocBean();
                        activityAttrDocBean.setAttrKey(attrValueAutowiredAnno.value());
                        activityAttrDocBean.setAttrType(variableElement.asType().toString());
                        activityAttrDocBeans.add(activityAttrDocBean);
                    }
                }
            }
            File file = new File(attrFolder, targetClassTypeMirror.toString() + ".json");
            if (file.exists()) {
                file.delete();
            }
            String json = gson.toJson(activityAttrDocBeans);
            Writer writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        }
    }

    private MethodSpec injectAttrValueMethod1(TypeElement targetClass) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("injectAttrValue")
                .addJavadoc("属性注入\n")
                .addParameter(
                        ParameterSpec
                                .builder(TypeName.get(targetClass.asType()), "target")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC);
        // 如果是 Activity
        if (mTypes.isSubtype(targetClass.asType(), activityTypeMirror)) {
            methodBuilder.addStatement("injectAttrValue(target, target.getIntent().getExtras())");
        } else if (mTypes.isSubtype(targetClass.asType(), fragmentTypeMirror)) {
            methodBuilder.addStatement("injectAttrValue(target, target.getArguments())");
        }
        return methodBuilder.build();
    }

    private MethodSpec injectAttrValueMethod2(TypeElement targetClass, Set<VariableElement> parameterFieldSet) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("injectAttrValue")
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
        methodBuilder.beginControlFlow("if(bundle == null)");
        methodBuilder.addStatement("return");
        methodBuilder.endControlFlow();
        // 循环没一个要注入的字段
        for (VariableElement variableElement : parameterFieldSet) {
            // 生成一个不重复的参数名字
            String parameterName = "target." + variableElement.getSimpleName();
            generateParameterCodeForInject(
                    variableElement, methodBuilder,
                    parameterName, "bundle"
            );
        }
        return methodBuilder.build();
    }

    private MethodSpec injectServiceMethod(TypeElement targetClass, Set<VariableElement> parameterFieldSet) {
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("injectService")
                .addJavadoc("Service注入\n")
                .addParameter(
                        ParameterSpec
                                .builder(TypeName.get(targetClass.asType()), "target")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC);
        // 循环没一个要注入的字段
        for (VariableElement variableElement : parameterFieldSet) {
            // 生成一个不重复的参数名字
            String parameterName = "target." + variableElement.getSimpleName();
            generateParameterCodeForInjectService(
                    variableElement, methodBuilder,
                    parameterName
            );
        }
        return methodBuilder.build();
    }

    public void generateParameterCodeForInject(VariableElement variableElement,
                                               MethodSpec.Builder methodBuilder,
                                               String parameterName,
                                               String bundleCallStr) {

        AttrValueAutowiredAnno attrValueAutowiredAnno = variableElement.getAnnotation(AttrValueAutowiredAnno.class);

        TypeMirror variableTypeMirror = variableElement.asType();
        TypeName parameterClassName = ClassName.get(variableTypeMirror);

        if (attrValueAutowiredAnno != null) {

            // 传值的 key 数组
            String[] keyNames = attrValueAutowiredAnno.value();
            if (keyNames.length == 1) {
                generateOneGetValueCode(variableElement, methodBuilder,
                        parameterName, bundleCallStr, variableTypeMirror,
                        parameterClassName, keyNames[0]);
            } else {
                for (int i = 0; i < keyNames.length; i++) {
                    String keyName = keyNames[i];
                    if (i == 0) {
                        methodBuilder.beginControlFlow("if($T.containsKey($N, $S))", parameterSupportTypeMirror, bundleCallStr,keyName);
                    } else {
                        methodBuilder.beginControlFlow("else if($T.containsKey($N, $S))", parameterSupportTypeMirror, bundleCallStr, keyName);
                    }
                    generateOneGetValueCode(variableElement, methodBuilder,
                            parameterName, bundleCallStr, variableTypeMirror,
                            parameterClassName, keyNames[i]);
                    methodBuilder.endControlFlow();
                }
            }

        }

    }

    private void generateOneGetValueCode(VariableElement variableElement,
                                         MethodSpec.Builder methodBuilder,
                                         String parameterName, String bundleCallStr,
                                         TypeMirror variableTypeMirror,
                                         TypeName parameterClassName, String keyName) {
        /**
         * 分三部分
         * 1: 基本类型和包装类型
         * 2: 其他声明的类型
         * 3: 数组类型
         */
        if (parameterClassName.equals(mClassNameString)) { // 如果是一个 String
            methodBuilder.addStatement("$N = $T.getString($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(charsequenceTypeName)) { // 如果是一个 charsequence
            methodBuilder.addStatement("$N = $T.getCharSequence($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.CHAR) || parameterClassName.equals(ClassName.CHAR.box())) { // 如果是一个 char or Char
            methodBuilder.addStatement("$N = $T.getChar($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.BYTE) || parameterClassName.equals(ClassName.BYTE.box())) { // 如果是一个byte or Byte
            methodBuilder.addStatement("$N = $T.getByte($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.SHORT) || parameterClassName.equals(ClassName.SHORT.box())) { // 如果是一个short or Short
            methodBuilder.addStatement("$N = $T.getShort($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.INT) || parameterClassName.equals(ClassName.INT.box())) { // 如果是一个int or Integer
            methodBuilder.addStatement("$N = $T.getInt($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.LONG) || parameterClassName.equals(ClassName.LONG.box())) { // 如果是一个long or Long
            methodBuilder.addStatement("$N = $T.getLong($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.FLOAT) || parameterClassName.equals(ClassName.FLOAT.box())) { // 如果是一个float or Float
            methodBuilder.addStatement("$N = $T.getFloat($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.DOUBLE) || parameterClassName.equals(ClassName.DOUBLE.box())) { // 如果是一个double or Double
            methodBuilder.addStatement("$N = $T.getDouble($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (parameterClassName.equals(ClassName.BOOLEAN) || parameterClassName.equals(ClassName.BOOLEAN.box())) { // 如果是一个boolean or Boolean
            methodBuilder.addStatement("$N = $T.getBoolean($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
        } else if (variableTypeMirror instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) variableTypeMirror;
            if (declaredType.asElement() instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) declaredType.asElement();
                ClassName className = ClassName.get(typeElement);
                if (arrayListClassName.equals(className)) { // 如果外面是 ArrayList
                    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                    if (typeArguments.size() == 1) { // 处理泛型个数是一个的
                        if (mTypes.isSubtype(typeArguments.get(0), parcelableTypeMirror)) { // 如果是 Parcelable 及其子类
                            methodBuilder.addStatement("$N = $T.getParcelableArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
                        }else if (mTypes.isSubtype(typeArguments.get(0), serializableTypeMirror)) { // 如果是 Serializable 及其子类
                            methodBuilder.addStatement("$N = $T.getSerializable($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
                        }  else if (mTypeElementString.asType().equals(typeArguments.get(0))) {
                            methodBuilder.addStatement("$N = $T.getStringArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
                        } else if (charsequenceTypeMirror.equals(typeArguments.get(0))) {
                            methodBuilder.addStatement("$N = $T.getCharSequenceArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
                        } else if (mTypeElementInteger.asType().equals(typeArguments.get(0))) {
                            methodBuilder.addStatement("$N = $T.getIntegerArrayList($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
                        }
                    }
                } else if (mClassNameSparseArray.equals(className)) { // 如果是 SparseArray
                    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                    if (typeArguments.size() == 1) { // 处理泛型个数是一个的
                        if (mTypes.isSubtype(typeArguments.get(0), parcelableTypeMirror)) { // 如果是 Parcelable 及其子类
                            methodBuilder.addStatement("$N = $T.getSparseParcelableArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
                        }
                    }
                } else { // 其他类型的情况,是实现序列化的对象,这种时候我们直接要从 bundle 中获取

                    boolean isSubParcelableType = mTypes.isSubtype(variableTypeMirror, parcelableTypeMirror);
                    boolean isSubSerializableType = mTypes.isSubtype(variableTypeMirror, serializableTypeMirror);
                    boolean isSubParcelableTypeAndSubSerializableType = isSubParcelableType && isSubSerializableType;

                    String isHaveValueName = "isHaveValue" + intCount.incrementAndGet();

                    if (isSubParcelableTypeAndSubSerializableType) {
                        methodBuilder.addStatement("boolean $N = false", isHaveValueName);
                    }
                    // 优先获取 parcelable
                    if (isSubParcelableType) {
                        String tempName = "temp" + intCount.incrementAndGet();
                        methodBuilder.addStatement("$T $N = $T.getParcelable($N, $S)", variableTypeMirror, tempName, parameterSupportTypeMirror, bundleCallStr, keyName);
                        methodBuilder.beginControlFlow("if ($N != null)", tempName);
                        methodBuilder.addStatement("$N = $N", parameterName, tempName);
                        if (isSubParcelableTypeAndSubSerializableType) {
                            methodBuilder.addStatement("$N = true", isHaveValueName);
                        }
                        methodBuilder.endControlFlow();
                    }
                    if (isSubSerializableType) {
                        if (isSubParcelableTypeAndSubSerializableType) {
                            methodBuilder.beginControlFlow("if (!$N)", isHaveValueName);
                        }
                        String tempName = "temp" + intCount.incrementAndGet();
                        methodBuilder.addStatement("$T $N = $T.getSerializable($N, $S)", variableTypeMirror, tempName, parameterSupportTypeMirror, bundleCallStr, keyName);
                        methodBuilder.beginControlFlow("if ($N != null)", tempName);
                        methodBuilder.addStatement("$N = $N", parameterName, tempName);
                        methodBuilder.endControlFlow();
                        if (isSubParcelableTypeAndSubSerializableType) {
                            methodBuilder.endControlFlow();
                        }
                    }
                }
            }
        } else if (variableTypeMirror instanceof ArrayType) { // 如果是数组
            ArrayType parameterArrayType = (ArrayType) variableTypeMirror;
            TypeName parameterComponentTypeName = ClassName.get(parameterArrayType.getComponentType());
            // 如果是一个 String[]
            if (parameterArrayType.getComponentType().equals(mTypeElementString.asType())) {
                methodBuilder.addStatement("$N = $T.getStringArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterArrayType.getComponentType().equals(charsequenceTypeElement.asType())) {
                methodBuilder.addStatement("$N = $T.getCharSequenceArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterArrayType.getComponentType().equals(mTypeElementString.asType())) {
                methodBuilder.addStatement("$N = $T.getStringArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.BYTE) || parameterComponentTypeName.equals(ClassName.BYTE.box())) { // 如果是 byte
                methodBuilder.addStatement("$N = $T.getByteArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.CHAR) || parameterComponentTypeName.equals(ClassName.CHAR.box())) { // 如果是 char
                methodBuilder.addStatement("$N = $T.getCharArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.SHORT) || parameterComponentTypeName.equals(ClassName.SHORT.box())) { // 如果是 short
                methodBuilder.addStatement("$N = $T.getShortArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.INT) || parameterComponentTypeName.equals(ClassName.INT.box())) { // 如果是 int
                methodBuilder.addStatement("$N = $T.getIntArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.LONG) || parameterComponentTypeName.equals(ClassName.LONG.box())) { // 如果是 long
                methodBuilder.addStatement("$N = $T.getLongArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.FLOAT) || parameterComponentTypeName.equals(ClassName.FLOAT.box())) { // 如果是 float
                methodBuilder.addStatement("$N = $T.getFloatArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.DOUBLE) || parameterComponentTypeName.equals(ClassName.DOUBLE.box())) { // 如果是 double
                methodBuilder.addStatement("$N = $T.getDoubleArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (parameterComponentTypeName.equals(ClassName.BOOLEAN) || parameterComponentTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是 boolean
                methodBuilder.addStatement("$N = $T.getBooleanArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else if (mTypes.isSameType(parameterArrayType.getComponentType(), parcelableTypeMirror)) {  // 如果是 Parcelable
                methodBuilder.addStatement("$N = $T.getParcelableArray($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, keyName, parameterName);
            } else {
                throw new ProcessException("can't to resolve unknow type parameter(" + variableElement.getEnclosingElement().getSimpleName() + "#" + variableElement.getSimpleName().toString() + ")");
            }
        }
    }

    public void generateParameterCodeForInjectService(
            VariableElement variableElement,
            MethodSpec.Builder methodBuilder,
            String parameterName) {

        ServiceAutowiredAnno serviceAutowiredAnno = variableElement.getAnnotation(ServiceAutowiredAnno.class);

        TypeMirror variableTypeMirror = variableElement.asType();
        TypeName parameterTypeName = ClassName.get(variableTypeMirror);

        if (serviceAutowiredAnno != null) {
            methodBuilder.addComment("may be null here");
            methodBuilder.addStatement("$N = $T.get($T.class)", parameterName, serviceTypeElement, parameterTypeName);
        }

    }


}
