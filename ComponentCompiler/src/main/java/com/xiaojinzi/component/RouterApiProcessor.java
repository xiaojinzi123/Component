package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAndPathAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.Navigate;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.anno.router.WithAnno;

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
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * 负责把像 Retrofit 那样的接口生成对应的实现类
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.ROUTERAPIANNO_CLASS_NAME})
public class RouterApiProcessor extends BaseProcessor {

    private TypeElement routerTypeElement;
    private TypeElement navigationDisposableTypeElement;
    private TypeMirror navigationDisposableTypeMirror;
    private TypeElement callBackTypeElement;
    private TypeMirror callBackTypeMirror;
    private TypeElement biCallBackTypeElement;
    private TypeMirror biCallBackTypeMirror;
    private TypeMirror callTypeMirror;
    private TypeMirror serializableTypeMirror;
    private TypeMirror parcelableTypeMirror;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        routerTypeElement = mElements.getTypeElement(ComponentConstants.ROUTER_CLASS_NAME);
        navigationDisposableTypeElement = mElements.getTypeElement(ComponentConstants.NAVIGATIONDISPOSABLE_CLASS_NAME);
        navigationDisposableTypeMirror = navigationDisposableTypeElement.asType();
        callBackTypeElement = mElements.getTypeElement(ComponentConstants.CALLBACK_CLASS_NAME);
        callBackTypeMirror = callBackTypeElement.asType();
        biCallBackTypeElement = mElements.getTypeElement(ComponentConstants.BICALLBACK_CLASS_NAME);
        biCallBackTypeMirror = biCallBackTypeElement.asType();
        callTypeMirror = mElements.getTypeElement(ComponentConstants.CALL_CLASS_NAME).asType();
        final TypeElement serializableTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.JAVA_SERIALIZABLE);
        serializableTypeMirror = serializableTypeElement.asType();
        final TypeElement parcelableTypeElement = mElements.getTypeElement(com.xiaojinzi.component.ComponentConstants.ANDROID_PARCELABLE);
        parcelableTypeMirror = parcelableTypeElement.asType();
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
                //.addModifiers(Modifier.PUBLIC)
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
        Navigate navigateAnnotation = executableElement.getAnnotation(Navigate.class);

        String host = hostAnnotation == null ? defaultHost : hostAnnotation.value();
        String path = pathAnnotation == null ? null : pathAnnotation.value();
        String hostAndPath = hostAndPathAnnotation == null ? null : hostAndPathAnnotation.value();

        boolean isReturnNavigationDisposable = false;
        boolean isReturnCall = false;

        // 返回的返回对象
        TypeMirror returnType = executableElement.getReturnType();
        isReturnNavigationDisposable = navigationDisposableTypeMirror.equals(returnType);
        isReturnCall = callTypeMirror.equals(returnType);
        // 方法名
        Name methodName = executableElement.getSimpleName();
        // 所有的方法的方法
        List<? extends VariableElement> parameters = executableElement.getParameters();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName.toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(returnType));

        // 找用什么上下文
        VariableElement withParameter = null;
        VariableElement callBackParameter = null;
        VariableElement biCallBackParameter = null;

        StringBuffer parameterStatement = new StringBuffer();
        List<Object> parameterArgs = new ArrayList<>();

        for (VariableElement parameter : parameters) {
            // 参数的类型
            TypeMirror parameterTypeMirror = parameter.asType();
            if (parameter.getAnnotation(WithAnno.class) != null) {
                withParameter = parameter;
            } else if (parameterTypeMirror.equals(callBackTypeMirror)) { // 如果是 CallBack
                callBackParameter = parameter;
            } else if (parameterTypeMirror.toString().startsWith(ComponentConstants.BICALLBACK_CLASS_NAME)) { // 如果是 BiCallback
                biCallBackParameter = parameter;
            } else { // 剩下的就都是参数的了
                ParameterAnno parameterParameterAnno = parameter.getAnnotation(ParameterAnno.class);
                if (parameterParameterAnno == null) {
                    throw new ProcessException("do you forget to add @ParameterAnno to you parameter(" + methodPath + "#" + parameter.getSimpleName().toString() + ")?");
                }
                TypeName parameterTypeName = ClassName.get(parameterTypeMirror);
                if (parameterTypeName.equals(mClassNameString)) {
                    parameterStatement.append("\n.putString($S,$N)");
                } else if (parameterTypeName.equals(ClassName.BYTE) || parameterTypeName.equals(ClassName.BYTE.box())) { // 如果是 byte
                    parameterStatement.append("\n.putByte($S,$N)");
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
        args.add(withParameter.getSimpleName().toString());

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

        // 根据跳转类型生成 navigate 方法
        if (navigateAnnotation == null) {
            if (!isReturnCall) {
                // 这里应该要报错
                //routerStatement.append("\n.你应该使用 @Navigate* 之类的注解标记这个方法的跳转类型");
                mMessager.printMessage(Diagnostic.Kind.ERROR, "you should use @Navigate* anno to flag Method: " + methodPath);
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

}
