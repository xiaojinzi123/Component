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
import com.xiaojinzi.component.anno.router.AfterActionAnno;
import com.xiaojinzi.component.anno.router.AfterErrorActionAnno;
import com.xiaojinzi.component.anno.router.AfterEventActionAnno;
import com.xiaojinzi.component.anno.router.AfterStartActionAnno;
import com.xiaojinzi.component.anno.router.BeforActionAnno;
import com.xiaojinzi.component.anno.router.BeforStartActionAnno;
import com.xiaojinzi.component.anno.router.CategoryAnno;
import com.xiaojinzi.component.anno.router.FlagAnno;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.NavigateAnno;
import com.xiaojinzi.component.anno.router.OptionsAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RequestCodeAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.anno.router.UseInteceptorAnno;
import com.xiaojinzi.component.anno.router.UserInfoAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * 负责把像 Retrofit 那样的接口生成对应的实现类
 * 支持基本版本和 Rx 版本
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.ROUTERAPIANNO_CLASS_NAME})
public class RouterApiProcessor extends BaseProcessor {

    private TypeElement charsequenceTypeElement;
    private TypeMirror charsequenceTypeMirror;
    private TypeName charsequenceTypeName;
    private TypeElement routerTypeElement;

    private TypeElement navigationDisposableTypeElement;
    private TypeMirror navigationDisposableTypeMirror;
    private TypeElement callBackTypeElement;
    private TypeMirror callBackTypeMirror;
    private TypeElement biCallBackTypeElement;
    private TypeMirror biCallBackTypeMirror;
    private TypeMirror biCallBackErasureTypeMirror;
    private TypeMirror callTypeMirror;

    // 这个也可能为空的
    private TypeElement routerRxTypeElement;

    // 这几个可能为null吧,因为没有依赖 RxJava
    private TypeMirror completableMirror;
    private TypeMirror singleMirror;
    private TypeMirror singleErasureMirror;

    private TypeMirror navigatorTypeMirror;
    private TypeMirror contextTypeMirror;
    private TypeMirror fragmentTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;
    private TypeMirror bundleTypeMirror;
    private ParameterizedTypeName intentComsumerParameterizedTypeName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        TypeElement completableTypeElement = mElements.getTypeElement(ComponentConstants.RXJAVA_COMPLETABLE);
        TypeElement singleTypeElement = mElements.getTypeElement(ComponentConstants.RXJAVA_SINGLE);

        completableMirror = completableTypeElement == null ? null : completableTypeElement.asType();
        singleMirror = singleTypeElement == null ? null : singleTypeElement.asType();
        singleErasureMirror = singleMirror == null ? null : processingEnv.getTypeUtils().erasure(singleMirror);

        charsequenceTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_CHARSEQUENCE);
        charsequenceTypeMirror = charsequenceTypeElement.asType();
        charsequenceTypeName = TypeName.get(charsequenceTypeMirror);
        routerTypeElement = mElements.getTypeElement(ComponentConstants.ROUTER_CLASS_NAME);
        routerRxTypeElement = mElements.getTypeElement(ComponentConstants.ROUTER_RX_CLASS_NAME);
        navigationDisposableTypeElement = mElements.getTypeElement(ComponentConstants.NAVIGATIONDISPOSABLE_CLASS_NAME);
        navigationDisposableTypeMirror = navigationDisposableTypeElement.asType();
        callBackTypeElement = mElements.getTypeElement(ComponentConstants.CALLBACK_CLASS_NAME);
        callBackTypeMirror = callBackTypeElement.asType();
        biCallBackTypeElement = mElements.getTypeElement(ComponentConstants.BICALLBACK_CLASS_NAME);
        biCallBackTypeMirror = biCallBackTypeElement.asType();
        biCallBackErasureTypeMirror = processingEnv.getTypeUtils().erasure(biCallBackTypeMirror);
        callTypeMirror = mElements.getTypeElement(ComponentConstants.CALL_CLASS_NAME).asType();
        navigatorTypeMirror = mElements.getTypeElement(ComponentConstants.NAVIGATOR_CLASS_NAME).asType();
        final TypeElement contextTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_CONTEXT);
        contextTypeMirror = contextTypeElement.asType();
        final TypeElement fragmentTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_FRAGMENT);
        fragmentTypeMirror = fragmentTypeElement.asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();
        final TypeElement bundleTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_BUNDLE);
        bundleTypeMirror = bundleTypeElement.asType();
        intentComsumerParameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(mElements.getTypeElement(ComponentConstants.CONSUMER_CLASS_NAME)),
                TypeName.get(mElements.getTypeElement(ComponentConstants.ANDROID_INTENT).asType())
        );

    }

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
     */
    private void createRouterApiImpl(TypeElement typeElement) {
        // 整个类默认的host注解
        HostAnno hostAnnotation = typeElement.getAnnotation(HostAnno.class);
        // 获取这个类默认的 host
        String defaultHost = hostAnnotation == null ? null : hostAnnotation.value();
        // 拿到全类型
        final String claName = typeElement.getQualifiedName().toString() + ComponentUtil.UIROUTERAPI;
        //pkg
        final String pkg = claName.substring(0, claName.lastIndexOf('.'));
        //simpleName
        final String cn = claName.substring(claName.lastIndexOf('.') + 1);
        // superClassName
        final ClassName superClass = ClassName.get(typeElement);
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(cn)
                .addAnnotation(mClassNameKeep)
                .addAnnotation(mClassNameComponentGeneratedAnno)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addSuperinterface(superClass);

        implementInterfaceMethods(typeSpecBuilder, typeElement, defaultHost);

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
     * @param defaultHost
     */
    private void implementInterfaceMethods(TypeSpec.Builder typeSpecBuilder, TypeElement apiTypeElement, String defaultHost) {
        // 拿出所有的方法
        List<? extends Element> enclosedElements = apiTypeElement.getEnclosedElements();
        for (Element methodElement : enclosedElements) {
            if (methodElement instanceof ExecutableElement) {
                implementInterfaceMethod(typeSpecBuilder, (ExecutableElement) methodElement, defaultHost);
            }
        }
    }

    private void implementInterfaceMethod(TypeSpec.Builder typeSpecBuilder, ExecutableElement executableElement, String defaultHost) {

        // 方法的一个调用 path,出错的时候展示用
        String methodPath = executableElement.getEnclosingElement().getSimpleName() + "#" + executableElement.getSimpleName();

        UserInfoAnno userInfoAnnotation = executableElement.getAnnotation(UserInfoAnno.class);
        HostAnno hostAnnotation = executableElement.getAnnotation(HostAnno.class);
        PathAnno pathAnnotation = executableElement.getAnnotation(PathAnno.class);
        HostAndPathAnno hostAndPathAnnotation = executableElement.getAnnotation(HostAndPathAnno.class);
        // 普通跳转的注解
        NavigateAnno navigateAnnotation = executableElement.getAnnotation(NavigateAnno.class);
        // 使用的拦截器
        UseInteceptorAnno useInteceptorAnnotation = executableElement.getAnnotation(UseInteceptorAnno.class);
        // 请求码的注解
        RequestCodeAnno requestCodeAnnotation = executableElement.getAnnotation(RequestCodeAnno.class);
        // intent flag 的注解
        FlagAnno flagAnnotation = executableElement.getAnnotation(FlagAnno.class);
        // intent category
        CategoryAnno categoryAnnotation = executableElement.getAnnotation(CategoryAnno.class);

        String userInfo = userInfoAnnotation == null ? null : userInfoAnnotation.value();
        String host = hostAnnotation == null ? defaultHost : hostAnnotation.value();
        String path = pathAnnotation == null ? null : pathAnnotation.value();
        String hostAndPath = hostAndPathAnnotation == null ? null : hostAndPathAnnotation.value();

        boolean isReturnNavigationDisposable = false, isReturnCall = false;
        boolean isReturnObservable = false, isReturnCompletable = false, isReturnSingle = false;

        // 返回的返回对象
        TypeMirror returnType = executableElement.getReturnType();
        isReturnNavigationDisposable = navigationDisposableTypeMirror.equals(returnType);
        isReturnCall = callTypeMirror.equals(returnType) || navigatorTypeMirror.equals(returnType);
        isReturnCompletable = returnType == null ? false : returnType.equals(completableMirror);
        isReturnSingle = returnType == null ? false : processingEnv.getTypeUtils().erasure(returnType).equals(singleErasureMirror);
        isReturnObservable = isReturnCompletable || isReturnSingle;

        // 方法名
        Name methodName = executableElement.getSimpleName();
        // 所有的方法的方法
        List<? extends VariableElement> parameters = executableElement.getParameters();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName.toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(returnType));

        VariableElement contextParameter = null;
        VariableElement fragmentParameter = null;
        VariableElement callBackParameter = null;
        VariableElement biCallBackParameter = null;
        VariableElement requestCodeParameter = null;
        VariableElement activityBundleOptionsParameter = null;
        VariableElement intentConsumerParameter = null;
        VariableElement beforActionParameter = null;
        VariableElement beforStartActionParameter = null;
        VariableElement afterStartActionParameter = null;
        VariableElement afterActionParameter = null;
        VariableElement afterErrorActionParameter = null;
        VariableElement afterEventActionParameter = null;

        StringBuffer parameterStatement = new StringBuffer();
        List<Object> parameterArgs = new ArrayList<>();

        for (VariableElement parameter : parameters) {
            // 参数的类型
            TypeMirror parameterTypeMirror = parameter.asType();
            if (mTypes.isSubtype(parameterTypeMirror, contextTypeMirror)) { // 如果是一个 Context
                contextParameter = parameter;
            } else if (mTypes.isSubtype(parameterTypeMirror, fragmentTypeMirror)) { // 如果是一个 Fragment
                fragmentParameter = parameter;
            } else if (mTypes.isSubtype(parameterTypeMirror, callBackTypeMirror)) { // 如果是 CallBack
                callBackParameter = parameter;
            } else if (mTypes.isSubtype(processingEnv.getTypeUtils().erasure(parameterTypeMirror), biCallBackErasureTypeMirror)) { // 如果是 BiCallback
                biCallBackParameter = parameter;
            } else if (parameter.getAnnotation(RequestCodeAnno.class) != null) { // 表示这是一个 requestCode 的参数值
                requestCodeParameter = parameter;
            } else if (parameter.getAnnotation(OptionsAnno.class) != null) { // 如果是 ActivityOptions 的Bundle
                activityBundleOptionsParameter = parameter;
            } else if (intentComsumerParameterizedTypeName.equals(TypeName.get(parameterTypeMirror))) { // 如果是 Consumer<Intent>
                intentConsumerParameter = parameter;
            } else if (parameter.getAnnotation(BeforActionAnno.class) != null) { // 如果是 beforAction
                beforActionParameter = parameter;
            } else if (parameter.getAnnotation(BeforStartActionAnno.class) != null) { // 如果是 beforStartAction
                beforStartActionParameter = parameter;
            } else if (parameter.getAnnotation(AfterStartActionAnno.class) != null) { // 如果是 afterStartAction
                afterStartActionParameter = parameter;
            } else if (parameter.getAnnotation(AfterActionAnno.class) != null) { // 如果是 afterAction
                afterActionParameter = parameter;
            } else if (parameter.getAnnotation(AfterErrorActionAnno.class) != null) { // 如果是 afterErrorAction
                afterErrorActionParameter = parameter;
            } else if (parameter.getAnnotation(AfterEventActionAnno.class) != null) { // 如果是 afterEventAction
                afterEventActionParameter = parameter;
            } else if (parameterTypeMirror.equals(bundleTypeMirror)) { // 如果是 Bundle,这个参数可以选填 @ParameterAnno 注解
                ParameterAnno parameterParameterAnno = parameter.getAnnotation(ParameterAnno.class);
                // 不填写的话就是 putAll 否则就是 putBundle
                if (parameterParameterAnno == null) {
                    parameterStatement.append("\n.putAll($N)");
                    parameterArgs.add(parameter.getSimpleName().toString());
                } else {
                    parameterStatement.append("\n.putBundle($S,$N)");
                    parameterArgs.add(parameterParameterAnno.value());
                    parameterArgs.add(parameter.getSimpleName().toString());
                }
            } else { // 剩下的就都是参数的了
                ParameterAnno parameterParameterAnno = parameter.getAnnotation(ParameterAnno.class);
                if (parameterParameterAnno == null) {
                    throw new ProcessException("do you forget to add @ParameterAnno to you parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")?");
                }
                TypeName parameterTypeName = TypeName.get(parameterTypeMirror);
                if (parameterTypeName.equals(mTypeNameString)) {
                    parameterStatement.append("\n.putString($S,$N)");
                } else if (parameterTypeName.equals(charsequenceTypeName)) {
                    parameterStatement.append("\n.putCharSequence($S,$N)");
                } else if (parameterTypeName.equals(ClassName.BYTE) || parameterTypeName.equals(ClassName.BYTE.box())) { // 如果是 byte
                    parameterStatement.append("\n.putByte($S,$N)");
                } else if (parameterTypeName.equals(ClassName.CHAR) || parameterTypeName.equals(ClassName.CHAR.box())) { // 如果是 char
                    parameterStatement.append("\n.putChar($S,$N)");
                } else if (parameterTypeName.equals(ClassName.SHORT) || parameterTypeName.equals(ClassName.SHORT.box())) { // 如果是 short
                    parameterStatement.append("\n.putShort($S,$N)");
                } else if (parameterTypeName.equals(ClassName.INT) || parameterTypeName.equals(ClassName.INT.box())) { // 如果是 int
                    parameterStatement.append("\n.putInt($S,$N)");
                } else if (parameterTypeName.equals(ClassName.LONG) || parameterTypeName.equals(ClassName.LONG.box())) { // 如果是 long
                    parameterStatement.append("\n.putLong($S,$N)");
                } else if (parameterTypeName.equals(ClassName.FLOAT) || parameterTypeName.equals(ClassName.FLOAT.box())) { // 如果是 float
                    parameterStatement.append("\n.putFloat($S,$N)");
                } else if (parameterTypeName.equals(ClassName.DOUBLE) || parameterTypeName.equals(ClassName.DOUBLE.box())) { // 如果是 double
                    parameterStatement.append("\n.putDouble($S,$N)");
                } else if (parameterTypeName.equals(ClassName.BOOLEAN) || parameterTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是 boolean
                    parameterStatement.append("\n.putBoolean($S,$N)");
                } else if (parameterTypeMirror instanceof DeclaredType) {
                    DeclaredType declaredType = (DeclaredType) parameterTypeMirror;
                    if (mTypeElementArrayList.asType().equals(declaredType.asElement().asType())) { // 如果外层是 ArrayList
                        // 泛型的类型
                        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                        if (typeArguments.size() == 1) {
                            if (mTypeElementString.asType().equals(typeArguments.get(0))) { // 如果是 String
                                parameterStatement.append("\n.putStringArrayList($S,$N)");
                            } else if (mTypeElementInteger.asType().equals(typeArguments.get(0))) { // 如果是 Integer
                                parameterStatement.append("\n.putIntegerArrayList($S,$N)");
                            } else if (mTypes.isSubtype(typeArguments.get(0), parcelableTypeMirror)) { // 如果是 Parcelable 及其子类
                                parameterStatement.append("\n.putParcelableArrayList($S,$N)");
                            } else if (mTypes.isSubtype(typeArguments.get(0), serializableTypeMirror)) { // 如果是 Serializable 及其子类
                                parameterStatement.append("\n.putSerializable($S,$N)");
                            } else if (charsequenceTypeMirror.equals(typeArguments.get(0))) { // 如果是 CharSequence
                                parameterStatement.append("\n.putCharSequenceArrayList($S,$N)");
                            } else {
                                throw new ProcessException("can't to resolve unknow type parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")");
                            }
                        } else {
                            throw new ProcessException("can't to resolve unknow type parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")");
                        }
                    } else if (mTypeElementSparseArray.asType().equals(declaredType.asElement().asType())) { // 如果是 SparseArray
                        // 泛型的类型
                        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                        if (mTypes.isSubtype(typeArguments.get(0), parcelableTypeMirror)) { // 如果是 Parcelable 及其子类
                            parameterStatement.append("\n.putSparseParcelableArray($S,$N)");
                        } else {
                            throw new ProcessException("can't to resolve unknow type parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")");
                        }
                    } else if (mTypes.isSubtype(parameterTypeMirror, parcelableTypeMirror)) {  // 如果是 Parcelable
                        parameterStatement.append("\n.putParcelable($S,$N)");
                    } else if (mTypes.isSubtype(parameterTypeMirror, serializableTypeMirror)) {  // 如果是 Serializable
                        parameterStatement.append("\n.putSerializable($S,$N)");
                    } else {
                        throw new ProcessException("can't to resolve unknow type parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")");
                    }
                } else if (parameterTypeMirror instanceof ArrayType) { // 如果是数组 []
                    ArrayType parameterArrayType = (ArrayType) parameterTypeMirror;
                    TypeName parameterComponentTypeName = ClassName.get(parameterArrayType.getComponentType());
                    // 如果是一个 String[]
                    if (parameterArrayType.getComponentType().equals(mTypeElementString.asType())) {
                        parameterStatement.append("\n.putStringArray($S,$N)");
                    } else if (parameterArrayType.getComponentType().equals(charsequenceTypeElement.asType())) {
                        parameterStatement.append("\n.putCharSequenceArray($S,$N)");
                    } else if (parameterArrayType.getComponentType().equals(mTypeElementString.asType())) {
                        parameterStatement.append("\n.putStringArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.BYTE) || parameterComponentTypeName.equals(ClassName.BYTE.box())) { // 如果是 byte
                        parameterStatement.append("\n.putByteArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.CHAR) || parameterComponentTypeName.equals(ClassName.CHAR.box())) { // 如果是 char
                        parameterStatement.append("\n.putCharArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.SHORT) || parameterComponentTypeName.equals(ClassName.SHORT.box())) { // 如果是 short
                        parameterStatement.append("\n.putShortArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.INT) || parameterComponentTypeName.equals(ClassName.INT.box())) { // 如果是 int
                        parameterStatement.append("\n.putIntArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.LONG) || parameterComponentTypeName.equals(ClassName.LONG.box())) { // 如果是 long
                        parameterStatement.append("\n.putLongArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.FLOAT) || parameterComponentTypeName.equals(ClassName.FLOAT.box())) { // 如果是 float
                        parameterStatement.append("\n.putFloatArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.DOUBLE) || parameterComponentTypeName.equals(ClassName.DOUBLE.box())) { // 如果是 double
                        parameterStatement.append("\n.putDoubleArray($S,$N)");
                    } else if (parameterComponentTypeName.equals(ClassName.BOOLEAN) || parameterComponentTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是 boolean
                        parameterStatement.append("\n.putBooleanArray($S,$N)");
                    } else if (mTypes.isSubtype(parameterArrayType.getComponentType(), parcelableTypeMirror)) {  // 如果是 Parcelable
                        parameterStatement.append("\n.putParcelableArray($S,$N)");
                    } else {
                        throw new ProcessException("can't to resolve unknow type parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")");
                    }
                } else {
                    throw new ProcessException("can't to resolve unknow type parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")");
                }
                parameterArgs.add(parameterParameterAnno.value());
                parameterArgs.add(parameter.getSimpleName().toString());
            }
            // 实现方法的参数对接
            methodBuilder.addParameter(ParameterSpec
                    .builder(
                            TypeName.get(parameterTypeMirror),
                            parameter.getSimpleName().toString()
                    )
                    .build()
            );
        }

        // 这里对所有用户写错的情况做一个检查

        if (isReturnObservable) {
            if (routerRxTypeElement == null) {
                throw new ProcessException("you must use router-rx lib,such as \n 'com.github.xiaojinzi123.Component:component-impl-rx:<version>' \n " +
                        "get more info please view \n https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE");
            }
            if (biCallBackParameter != null) {
                throw new ProcessException("the parameter " + biCallBackParameter.getSimpleName() +
                        " of method " + methodPath + " is not allow when then returnType is " + ComponentConstants.RXJAVA_COMPLETABLE + " or " + ComponentConstants.RXJAVA_SINGLE);
            }
            if (callBackParameter != null) {
                throw new ProcessException("the parameter " + callBackParameter.getSimpleName() +
                        " of method " + methodPath + " is not allow when then returnType is " + ComponentConstants.RXJAVA_COMPLETABLE + " or " + ComponentConstants.RXJAVA_SINGLE);
            }
            if (navigateAnnotation == null) {
                if (!isReturnCompletable) {
                    throw new ProcessException("the returnType of method (" + methodPath + ") must be " + ComponentConstants.RXJAVA_COMPLETABLE);
                }
            } else {
                if (navigateAnnotation.forResult()) {
                    if (!isReturnSingle) {
                        throw new ProcessException("the returnType of method (" + methodPath + ") must be " + ComponentConstants.RXJAVA_SINGLE + "<ActivityResult>");
                    }
                } else if (navigateAnnotation.forIntent()) {
                    if (!isReturnSingle) {
                        throw new ProcessException("the returnType of method (" + methodPath + ") must be " + ComponentConstants.RXJAVA_SINGLE + "<Intent>");
                    }
                } else if (navigateAnnotation.forResultCode()) {
                    if (!isReturnSingle) {
                        throw new ProcessException("the returnType of method (" + methodPath + ") must be " + ComponentConstants.RXJAVA_SINGLE + "<Integer>");
                    }
                } else {
                    if (!isReturnCompletable) {
                        throw new ProcessException("the returnType of method (" + methodPath + ") must be " + ComponentConstants.RXJAVA_COMPLETABLE);
                    }
                }
            }
        } else {
            if (navigateAnnotation == null) {
                if (biCallBackParameter != null) {
                    throw new ProcessException("the parameter " + biCallBackParameter.getSimpleName() +
                            " of method " + methodPath + " is not allow when then returnType is " + ComponentConstants.RXJAVA_COMPLETABLE + " or " + ComponentConstants.RXJAVA_SINGLE);
                }
            } else {
                if (navigateAnnotation.forResult()) {
                    if (biCallBackParameter == null) {
                        throw new ProcessException("do you forget to add parameter(" + ComponentConstants.BICALLBACK_CLASS_NAME + "<ActivityResult>) to you method(" + methodPath + ")?");
                    }
                } else if (navigateAnnotation.forIntent()) {
                    if (biCallBackParameter == null) {
                        throw new ProcessException("do you forget to add parameter(" + ComponentConstants.BICALLBACK_CLASS_NAME + "<Intent>) to you method(" + methodPath + ")?");
                    }
                } else if (navigateAnnotation.forResultCode()) {
                    if (biCallBackParameter == null) {
                        throw new ProcessException("do you forget to add parameter(" + ComponentConstants.BICALLBACK_CLASS_NAME + "<Integer>) to you method(" + methodPath + ")?");
                    }
                } else {
                    // 说明是想匹配 resultCode
                    if (navigateAnnotation.resultCodeMatch() != Integer.MIN_VALUE) {
                        if (callBackParameter == null) {
                            throw new ProcessException("do you forget to add parameter(" + ComponentConstants.CALLBACK_CLASS_NAME + ") to you method(" + methodPath + ")?");
                        }
                    }
                }
            }
        }

        StringBuffer routerStatement = new StringBuffer();
        List<Object> args = new ArrayList<>();

        // 根据返回值最终的结尾

        if (isReturnNavigationDisposable || isReturnCall || isReturnObservable) {
            routerStatement.append("return ");
        }

        // with 方法ok
        routerStatement.append("$T.with($N)");
        if (isReturnObservable) {
            args.add(routerRxTypeElement);
        } else {
            args.add(routerTypeElement);
        }
        if (contextParameter != null) {
            args.add(contextParameter.getSimpleName().toString());
        } else if (fragmentParameter != null) {
            args.add(fragmentParameter.getSimpleName().toString());
        } else {
            args.add("");
            // throw new ProcessException("do you forget to add a 'Context' or 'Activity' or 'androidx.fragment.app.Fragment' parameter to method(" + methodPath + ") ?");
        }

        if (userInfo != null) {
            routerStatement.append("\n.userInfo($S)");
            args.add(userInfo);
        }

        // host 和 path
        if (hostAndPath == null) { // 采用 host 和 path 方法
            routerStatement.append("\n.host($S)");
            routerStatement.append("\n.path($S)");
            args.add(host);
            args.add(path);
        } else {
            routerStatement.append("\n.hostAndPath($S)");
            args.add(hostAndPath);
        }

        // 各种参数设置

        routerStatement.append(parameterStatement.toString());
        args.addAll(parameterArgs);

        // requestCode 的读取,参数传入的优先
        if (requestCodeParameter != null) {
            TypeName requestCodeTypeName = TypeName.get(requestCodeParameter.asType());
            if (requestCodeTypeName.equals(ClassName.INT) || requestCodeTypeName.equals(ClassName.INT.box())) {
                routerStatement.append("\n.requestCode($N)");
                args.add(requestCodeParameter.getSimpleName().toString());
            } else {
                throw new ProcessException("the class type of parameter(" + methodPath + "#" + requestCodeParameter.getSimpleName() + ") must be a int or Integer");
            }
        } else if (requestCodeAnnotation != null) {
            if (requestCodeAnnotation.value() == Integer.MIN_VALUE) { // 如果用户没有写
                routerStatement.append("\n.requestCodeRandom()");
            } else {
                routerStatement.append("\n.requestCode($L)");
                args.add(requestCodeAnnotation.value());
            }
        }

        // 使用拦截器
        if (useInteceptorAnnotation != null) {
            StringBuffer interceptorStatement = new StringBuffer();
            List<Object> interceptorArgs = new ArrayList<>();
            if (useInteceptorAnnotation.names().length > 0) {
                for (int i = 0; i < useInteceptorAnnotation.names().length; i++) {
                    String interceptorName = useInteceptorAnnotation.names()[i];
                    if (i == 0) {
                        interceptorStatement.append("$S");
                    } else {
                        interceptorStatement.append(",").append("$S");
                    }
                    interceptorArgs.add(interceptorName);
                }
                routerStatement.append("\n.interceptorNames(" + interceptorStatement.toString() + ")");
                args.addAll(interceptorArgs);
            }
            interceptorStatement.delete(0, interceptorStatement.length());
            interceptorArgs.clear();
            List<String> implClassName = getImplClassName(useInteceptorAnnotation);
            if (implClassName.size() > 0) {
                for (int i = 0; i < implClassName.size(); i++) {
                    // initMapMethodSpecBuilder.addStatement("$N.add($T.class)", interceptorListName, ClassName.get(mElements.getTypeElement(interceptorClassName)));
                    if (i == 0) {
                        interceptorStatement.append("$T.class");
                    } else {
                        interceptorStatement.append(",").append("$T.class");
                    }
                    interceptorArgs.add(ClassName.get(mElements.getTypeElement(implClassName.get(i))));
                }
                routerStatement.append("\n.interceptors(" + interceptorStatement.toString() + ")");
                args.addAll(interceptorArgs);
            }
        }

        // 给路由添加 flags

        if (flagAnnotation != null) {
            int[] flags = flagAnnotation.value();
            for (int flag : flags) {
                routerStatement.append("\n.addIntentFlags($L)");
                args.add(flag);
            }
        }

        // 给路由添加 categories

        if (categoryAnnotation != null) {
            String[] categories = categoryAnnotation.value();
            for (String category : categories) {
                routerStatement.append("\n.addIntentCategories($S)");
                args.add(category);
            }
        }

        // 添加 activity options

        if (activityBundleOptionsParameter != null) {
            routerStatement.append("\n.options($N)");
            args.add(activityBundleOptionsParameter.getSimpleName().toString());
        }

        // Consumer<Intent>

        if (intentConsumerParameter != null) {
            routerStatement.append("\n.intentConsumer($N)");
            args.add(intentConsumerParameter.getSimpleName().toString());
        }

        // 几个 action
        if (beforActionParameter != null) {
            routerStatement.append("\n.beforAction($N)");
            args.add(beforActionParameter.getSimpleName().toString());
        }
        if (beforStartActionParameter != null) {
            routerStatement.append("\n.beforStartAction($N)");
            args.add(beforStartActionParameter.getSimpleName().toString());
        }
        if (afterStartActionParameter != null) {
            routerStatement.append("\n.afterStartAction($N)");
            args.add(afterStartActionParameter.getSimpleName().toString());
        }
        if (afterActionParameter != null) {
            routerStatement.append("\n.afterAction($N)");
            args.add(afterActionParameter.getSimpleName().toString());
        }
        if (afterErrorActionParameter != null) {
            routerStatement.append("\n.afterErrorAction($N)");
            args.add(afterErrorActionParameter.getSimpleName().toString());
        }
        if (afterEventActionParameter != null) {
            routerStatement.append("\n.afterEventAction($N)");
            args.add(afterEventActionParameter.getSimpleName().toString());
        }

        // 根据跳转类型生成 navigate 方法
        if (navigateAnnotation == null) {
            if (isReturnObservable) {
                routerStatement.append("\n.call()");
            } else if (!isReturnCall) {
                if (callBackParameter == null) {
                    routerStatement.append("\n.navigate()");
                } else {
                    routerStatement.append("\n.navigate($N)");
                    args.add(callBackParameter.getSimpleName().toString());
                }
            }
        } else {
            if (isReturnObservable) {
                if (navigateAnnotation.forResult()) {
                    routerStatement.append("\n.activityResultCall()");
                } else if (navigateAnnotation.forIntent()) {
                    if (navigateAnnotation.resultCodeMatch() == Integer.MIN_VALUE) { // 表示用户没写
                        routerStatement.append("\n.intentCall()");
                    } else {
                        routerStatement.append("\n.intentResultCodeMatchCall($L)");
                        args.add(navigateAnnotation.resultCodeMatch());
                    }
                } else if (navigateAnnotation.forResultCode()) {
                    routerStatement.append("\n.resultCodeCall()");
                } else {
                    if (navigateAnnotation.resultCodeMatch() == Integer.MIN_VALUE) {
                        routerStatement.append("\n.call()");
                    } else {
                        routerStatement.append("\n.resultCodeMatchCall($L)");
                        args.add(navigateAnnotation.resultCodeMatch());
                    }
                }
            } else {
                if (navigateAnnotation.forResult()) {
                    routerStatement.append("\n.navigateForResult($N)");
                    args.add(biCallBackParameter.getSimpleName().toString());
                } else if (navigateAnnotation.forIntent()) {
                    if (navigateAnnotation.resultCodeMatch() == Integer.MIN_VALUE) { // 表示用户没写
                        routerStatement.append("\n.navigateForIntent($N)");
                        args.add(biCallBackParameter.getSimpleName().toString());
                    } else {
                        routerStatement.append("\n.navigateForIntentAndResultCodeMatch($N,$L)");
                        args.add(biCallBackParameter.getSimpleName().toString());
                        args.add(navigateAnnotation.resultCodeMatch());
                    }
                } else if (navigateAnnotation.forResultCode()) {
                    routerStatement.append("\n.navigateForResultCode($N)");
                    args.add(biCallBackParameter.getSimpleName().toString());
                } else {
                    if (navigateAnnotation.resultCodeMatch() == Integer.MIN_VALUE) {
                        if (callBackParameter == null) {
                            routerStatement.append("\n.navigate()");
                        } else {
                            routerStatement.append("\n.navigate($N)");
                            args.add(callBackParameter.getSimpleName().toString());
                        }
                    } else { // 为了匹配 resultCode
                        routerStatement.append("\n.navigateForResultCodeMatch($N,$L)");
                        args.add(callBackParameter.getSimpleName().toString());
                        args.add(navigateAnnotation.resultCodeMatch());
                    }
                }
            }
        }

        methodBuilder.addStatement(routerStatement.toString(), args.toArray());

        typeSpecBuilder.addMethod(methodBuilder.build());

    }

    private List<String> getImplClassName(UseInteceptorAnno anno) {
        List<String> implClassNames = new ArrayList<>();
        try {
            implClassNames.clear();
            //这里会报错，此时在catch中获取到拦截器的全类名
            final Class[] interceptors = anno.classes();
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


}
