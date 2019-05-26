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
import com.xiaojinzi.component.anno.router.CategoryAnno;
import com.xiaojinzi.component.anno.router.FlagAnno;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.NavigateAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RequestCodeAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.anno.router.UseInteceptorAnno;

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
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * 负责把像 Retrofit 那样的接口生成对应的实现类
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.ROUTERAPIANNO_CLASS_NAME})
public class RouterApiProcessor extends BaseProcessor {

    private TypeElement charsequenceTypeElement;
    private TypeMirror charsequenceTypeMirror;
    private ClassName charsequenceClassName;
    private TypeName charsequenceTypeName;
    private TypeElement routerTypeElement;
    private TypeElement navigationDisposableTypeElement;
    private TypeMirror navigationDisposableTypeMirror;
    private TypeElement callBackTypeElement;
    private TypeMirror callBackTypeMirror;
    private TypeElement biCallBackTypeElement;
    private TypeMirror biCallBackTypeMirror;
    private TypeMirror callTypeMirror;
    private TypeMirror navigatorTypeMirror;
    private TypeMirror contextTypeMirror;
    private TypeMirror fragmentTypeMirror;
    private TypeMirror activityTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;
    private TypeMirror bundleTypeMirror;
    private ParameterizedTypeName stringArrayListParameterizedTypeName;
    private ParameterizedTypeName integerArrayListParameterizedTypeName;
    private ParameterizedTypeName parcelableArrayListParameterizedTypeName;
    private ParameterizedTypeName charsequenceArrayListParameterizedTypeName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        charsequenceTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_CHARSEQUENCE);
        charsequenceTypeMirror = charsequenceTypeElement.asType();
        charsequenceClassName = ClassName.get(charsequenceTypeElement);
        charsequenceTypeName = TypeName.get(charsequenceTypeMirror);
        routerTypeElement = mElements.getTypeElement(ComponentConstants.ROUTER_CLASS_NAME);
        navigationDisposableTypeElement = mElements.getTypeElement(ComponentConstants.NAVIGATIONDISPOSABLE_CLASS_NAME);
        navigationDisposableTypeMirror = navigationDisposableTypeElement.asType();
        callBackTypeElement = mElements.getTypeElement(ComponentConstants.CALLBACK_CLASS_NAME);
        callBackTypeMirror = callBackTypeElement.asType();
        biCallBackTypeElement = mElements.getTypeElement(ComponentConstants.BICALLBACK_CLASS_NAME);
        biCallBackTypeMirror = biCallBackTypeElement.asType();
        callTypeMirror = mElements.getTypeElement(ComponentConstants.CALL_CLASS_NAME).asType();
        navigatorTypeMirror = mElements.getTypeElement(ComponentConstants.NAVIGATOR_CLASS_NAME).asType();
        final TypeElement contextTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_CONTEXT);
        contextTypeMirror = contextTypeElement.asType();
        final TypeElement fragmentTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_V4_FRAGMENT);
        fragmentTypeMirror = fragmentTypeElement.asType();
        final TypeElement activityTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_ACTIVITY);
        activityTypeMirror = activityTypeElement.asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();
        final TypeElement bundleTypeElement = mElements.getTypeElement(ComponentConstants.ANDROID_BUNDLE);
        bundleTypeMirror = bundleTypeElement.asType();
        stringArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, mClassNameString);
        integerArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, ClassName.INT.box());
        parcelableArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, TypeName.get(parcelableTypeMirror));
        charsequenceArrayListParameterizedTypeName = ParameterizedTypeName.get(mClassNameArrayList, TypeName.get(charsequenceTypeMirror));
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
     *
     * @param typeElement
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

        String host = hostAnnotation == null ? defaultHost : hostAnnotation.value();
        String path = pathAnnotation == null ? null : pathAnnotation.value();
        String hostAndPath = hostAndPathAnnotation == null ? null : hostAndPathAnnotation.value();

        boolean isReturnNavigationDisposable = false;
        boolean isReturnCall = false;

        // 返回的返回对象
        TypeMirror returnType = executableElement.getReturnType();
        isReturnNavigationDisposable = navigationDisposableTypeMirror.equals(returnType);
        isReturnCall = callTypeMirror.equals(returnType) || navigatorTypeMirror.equals(returnType);
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

        StringBuffer parameterStatement = new StringBuffer();
        List<Object> parameterArgs = new ArrayList<>();

        for (VariableElement parameter : parameters) {
            // 参数的类型
            TypeMirror parameterTypeMirror = parameter.asType();
            List<? extends TypeMirror> typeMirrors = mTypes.directSupertypes(parameterTypeMirror);
            if (mTypes.isSubtype(parameterTypeMirror, contextTypeMirror)) { // mTypes.isSubtype(parameterTypeMirror, fragmentTypeMirror)
                contextParameter = parameter;
            } else if (mTypes.isSubtype(parameterTypeMirror, fragmentTypeMirror)) {
                fragmentParameter = parameter;
            } else if (parameterTypeMirror.equals(callBackTypeMirror)) { // 如果是 CallBack
                callBackParameter = parameter;
            } else if (parameterTypeMirror.toString().startsWith(ComponentConstants.BICALLBACK_CLASS_NAME)) { // 如果是 BiCallback
                biCallBackParameter = parameter;
            } else if (parameter.getAnnotation(RequestCodeAnno.class) != null) { // 表示这是一个 requestCode 的参数值
                requestCodeParameter = parameter;
            } else if (parameterTypeMirror.equals(bundleTypeMirror)) { // 如果是 Bundle,这个参数可以选填 @ParameterAnno 注解
                ParameterAnno parameterParameterAnno = parameter.getAnnotation(ParameterAnno.class);
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
                } else if (stringArrayListParameterizedTypeName.equals(TypeName.get(parameterTypeMirror))) {
                    parameterStatement.append("\n.putStringArrayList($S,$N)");
                } else if (integerArrayListParameterizedTypeName.equals(TypeName.get(parameterTypeMirror))) {
                    parameterStatement.append("\n.putIntegerArrayList($S,$N)");
                } else if (parcelableArrayListParameterizedTypeName.equals(TypeName.get(parameterTypeMirror))) {
                    parameterStatement.append("\n.putParcelableArrayList($S,$N)");
                } else if (charsequenceArrayListParameterizedTypeName.equals(TypeName.get(parameterTypeMirror))) {
                    parameterStatement.append("\n.putCharSequenceArrayList($S,$N)");
                } else if (parameterTypeMirror instanceof ArrayType) {
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
                } else if (mTypes.isSubtype(parameterTypeMirror, parcelableTypeMirror)) {  // 如果是 Parcelable
                    parameterStatement.append("\n.putParcelable($S,$N)");
                } else if (mTypes.isSubtype(parameterTypeMirror, serializableTypeMirror)) {  // 如果是 Serializable
                    parameterStatement.append("\n.putSerializable($S,$N)");
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

        StringBuffer routerStatement = new StringBuffer();
        List<Object> args = new ArrayList<>();

        // with 方法ok
        routerStatement.append("$T.with($N)");
        args.add(routerTypeElement);
        if (contextParameter != null) {
            args.add(contextParameter.getSimpleName().toString());
        } else if (fragmentParameter != null) {
            args.add(fragmentParameter.getSimpleName().toString());
        } else {
            throw new ProcessException("do you forget to add a 'Context' or 'Activity' or 'android.support.v4.app.Fragment' parameter to method(" + methodPath + ") ?");
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

        // 根据跳转类型生成 navigate 方法
        if (navigateAnnotation == null) {
            if (isReturnCall) {
            } else {
                if (callBackParameter == null) {
                    routerStatement.append("\n.navigate()");
                } else {
                    routerStatement.append("\n.navigate($N)");
                    args.add(callBackParameter.getSimpleName().toString());
                }
            }
        } else {
            if (navigateAnnotation.forResult()) {
                if (biCallBackParameter == null) {
                    throw new ProcessException("do you forget to add parameter(" + ComponentConstants.BICALLBACK_CLASS_NAME + ") to you method(" + methodPath + ")?");
                }
                routerStatement.append("\n.navigateForResult($N)");
                args.add(biCallBackParameter.getSimpleName().toString());
            } else if (navigateAnnotation.forIntent()) {
                if (biCallBackParameter == null) {
                    throw new ProcessException("do you forget to add parameter(" + ComponentConstants.BICALLBACK_CLASS_NAME + ") to you method(" + methodPath + ")?");
                }
                if (navigateAnnotation.resultCodeMatch() == Integer.MIN_VALUE) {
                    routerStatement.append("\n.navigateForIntent($N)");
                    args.add(biCallBackParameter.getSimpleName().toString());
                } else {
                    routerStatement.append("\n.navigateForIntentAndResultCodeMatch($N,$L)");
                    args.add(biCallBackParameter.getSimpleName().toString());
                    args.add(navigateAnnotation.resultCodeMatch());
                }
            } else if (navigateAnnotation.forResultCode()) {
                if (biCallBackParameter == null) {
                    throw new ProcessException("do you forget to add parameter(" + ComponentConstants.BICALLBACK_CLASS_NAME + ") to you method(" + methodPath + ")?");
                }
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
                    if (callBackParameter == null) {
                        throw new ProcessException("do you forget to add parameter(" + ComponentConstants.CALLBACK_CLASS_NAME + ") to you method(" + methodPath + ")?");
                    }
                    routerStatement.append("\n.navigateForResultCodeMatch($N,$L)");
                    args.add(callBackParameter.getSimpleName().toString());
                    args.add(navigateAnnotation.resultCodeMatch());
                }
            }
        }

        // 根据返回值最终的结尾

        if (isReturnNavigationDisposable || isReturnCall) {
            routerStatement.insert(0, "return ");
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
