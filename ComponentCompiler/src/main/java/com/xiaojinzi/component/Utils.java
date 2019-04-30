package com.xiaojinzi.component;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.parameter.BooleanDefaultAnno;
import com.xiaojinzi.component.anno.parameter.ByteDefaultAnno;
import com.xiaojinzi.component.anno.parameter.DoubleDefaultAnno;
import com.xiaojinzi.component.anno.parameter.FloatDefaultAnno;
import com.xiaojinzi.component.anno.parameter.IntDefaultAnno;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.parameter.LongDefaultAnno;
import com.xiaojinzi.component.anno.parameter.ShortDefaultAnno;
import com.xiaojinzi.component.anno.parameter.StringDefaultAnno;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

class Utils {

    public static void generateParameterCodeForRouter(Types mTypes,
                                                      VariableElement variableElement,
                                                      MethodSpec.Builder methodBuilder,
                                                      TypeMirror parameterSupportTypeMirror,
                                                      String parameterName,
                                                      String bundleCallStr,
                                                      ClassName stringClassName,
                                                      TypeMirror serializableTypeMirror,
                                                      TypeMirror parcelableTypeMirror) {
        TypeName parameterTypeName = ClassName.get(variableElement.asType());
        ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
        if (parameterTypeName.equals(stringClassName)) { // 如果是一个 String
            StringDefaultAnno stringDefaultAnno = variableElement.getAnnotation(StringDefaultAnno.class);
            if (stringDefaultAnno == null) {
                methodBuilder.addStatement("String $N = $T.getString($N,$S)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value());
            } else {
                methodBuilder.addStatement("String $N = $T.getString($N,$S,$S)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), stringDefaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.BYTE)) { // 如果是一个 byte
            ByteDefaultAnno defaultAnno = variableElement.getAnnotation(ByteDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("byte $N = $T.getByte($N,$S,(byte)$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), 0);
            } else {
                methodBuilder.addStatement("byte $N = $T.getByte($N,$S,(byte)$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.BYTE.box())) { // 如果是一个 Byte
            ByteDefaultAnno defaultAnno = variableElement.getAnnotation(ByteDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("Byte $N = $T.getByte($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Byte $N = $T.getByte($N,$S,(byte)$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.SHORT)) { // 如果是一个short or Short
            ShortDefaultAnno defaultAnno = variableElement.getAnnotation(ShortDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("short $N = $T.getShort($N,$S,(short)$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), 0);
            } else {
                methodBuilder.addStatement("short $N = $T.getShort($N,$S,(short)$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.SHORT.box())) { // 如果是一个short or Short
            ShortDefaultAnno defaultAnno = variableElement.getAnnotation(ShortDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("Short $N = $T.getShort($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Short $N = $T.getShort($N,$S,(short)$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.INT)) { // 如果是一个int
            IntDefaultAnno intDefaultAnno = variableElement.getAnnotation(IntDefaultAnno.class);
            if (intDefaultAnno == null) {
                methodBuilder.addStatement("int $N = $T.getInt($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), 0);
            } else {
                methodBuilder.addStatement("int $N = $T.getInt($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), intDefaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.INT.box())) { // 如果是一个 Integer
            IntDefaultAnno intDefaultAnno = variableElement.getAnnotation(IntDefaultAnno.class);
            if (intDefaultAnno == null) {
                methodBuilder.addStatement("Integer $N = $T.getInt($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Integer $N = $T.getInt($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), intDefaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.LONG)) { // 如果是一个 long
            LongDefaultAnno defaultAnno = variableElement.getAnnotation(LongDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("long $N = $T.getLong($N,$S,$Ll)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), 0);
            } else {
                methodBuilder.addStatement("long $N = $T.getLong($N,$S,$Ll)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.LONG.box())) { // 如果是一个 Long
            LongDefaultAnno defaultAnno = variableElement.getAnnotation(LongDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("Long $N = $T.getLong($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Long $N = $T.getLong($N,$S,$Ll)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.FLOAT)) { // 如果是一个 float
            FloatDefaultAnno defaultAnno = variableElement.getAnnotation(FloatDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("float $N = $T.getFloat($N,$S,$Lf)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), 0);
            } else {
                methodBuilder.addStatement("float $N = $T.getFloat($N,$S,$Lf)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.FLOAT.box())) { // 如果是一个 Float
            FloatDefaultAnno defaultAnno = variableElement.getAnnotation(FloatDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("Float $N = $T.getFloat($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Float $N = $T.getFloat($N,$S,$Lf)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.DOUBLE)) { // 如果是一个 double
            DoubleDefaultAnno defaultAnno = variableElement.getAnnotation(DoubleDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("double $N = $T.getDouble($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), 0.0);
            } else {
                methodBuilder.addStatement("double $N = $T.getDouble($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.DOUBLE.box())) { // 如果是一个 DOUBLE
            DoubleDefaultAnno defaultAnno = variableElement.getAnnotation(DoubleDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("Double $N = $T.getDouble($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Double $N = $T.getDouble($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.BOOLEAN)) { // 如果是一个 boolean
            BooleanDefaultAnno defaultAnno = variableElement.getAnnotation(BooleanDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("boolean $N = $T.getBoolean($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), false);
            } else {
                methodBuilder.addStatement("boolean $N = $T.getBoolean($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else if (parameterTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是一个 Boolean
            BooleanDefaultAnno defaultAnno = variableElement.getAnnotation(BooleanDefaultAnno.class);
            if (defaultAnno == null) {
                methodBuilder.addStatement("Boolean $N = $T.getBoolean($N,$S,$N)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), "null");
            } else {
                methodBuilder.addStatement("Boolean $N = $T.getBoolean($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), defaultAnno.value());
            }
        } else { // 其他类型的情况,是实现序列化的对象,这种时候我们直接要从 bundle 中获取
            methodBuilder.addStatement("$T $N = null", variableElement.asType(), parameterName);
            // 优先获取 parcelable
            if (mTypes.isSubtype(variableElement.asType(), parcelableTypeMirror)) {
                methodBuilder.addStatement("$N = ($T) $N.getParcelable($S)", parameterName, variableElement.asType(), bundleCallStr, parameterAnno.value());
            }
            methodBuilder.beginControlFlow("if ($N == null) ", parameterName);
            if (mTypes.isSubtype(variableElement.asType(), serializableTypeMirror)) {
                methodBuilder.addStatement("$N = ($T) $N.getSerializable($S)", parameterName, variableElement.asType(), bundleCallStr, parameterAnno.value());
            }
            methodBuilder.endControlFlow();
        }
    }

    public static void generateParameterCodeForInject(Types mTypes,
                                                      VariableElement variableElement,
                                                      MethodSpec.Builder methodBuilder,
                                                      TypeMirror parameterSupportTypeMirror,
                                                      String parameterName,
                                                      String bundleCallStr,
                                                      ClassName stringClassName,
                                                      TypeMirror serializableTypeMirror,
                                                      TypeMirror parcelableTypeMirror) {
        TypeName parameterTypeName = ClassName.get(variableElement.asType());
        if (parameterTypeName.equals(stringClassName)) { // 如果是一个 String
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getString($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.BYTE) || parameterTypeName.equals(ClassName.BYTE.box())) { // 如果是一个byte or Byte
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getByte($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.SHORT) || parameterTypeName.equals(ClassName.SHORT.box())) { // 如果是一个short or Short
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getShort($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.INT) || parameterTypeName.equals(ClassName.INT.box())) { // 如果是一个int or Integer
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getInt($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.LONG) || parameterTypeName.equals(ClassName.LONG.box())) { // 如果是一个long or Long
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getLong($N,$S,$Ll)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.FLOAT) || parameterTypeName.equals(ClassName.FLOAT.box())) { // 如果是一个float or Float
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getFloat($N,$S,$Lf)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.DOUBLE) || parameterTypeName.equals(ClassName.DOUBLE.box())) { // 如果是一个double or Double
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getDouble($N,$S,$Ld)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else if (parameterTypeName.equals(ClassName.BOOLEAN) || parameterTypeName.equals(ClassName.BOOLEAN.box())) { // 如果是一个boolean or Boolean
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("$N = $T.getBoolean($N,$S,$L)", parameterName, parameterSupportTypeMirror, bundleCallStr, parameterAnno.value(), parameterName);
        } else { // 其他类型的情况,是实现序列化的对象,这种时候我们直接要从 bundle 中获取
            ParameterAnno parameterAnno = variableElement.getAnnotation(ParameterAnno.class);
            methodBuilder.addStatement("boolean isHaveValue = false");
            // 优先获取 parcelable
            if (mTypes.isSubtype(variableElement.asType(), parcelableTypeMirror)) {
                methodBuilder.beginControlFlow("if ($N.containsKey($S) && $N.getParcelable($S) != null)", bundleCallStr, parameterAnno.value(), bundleCallStr, parameterAnno.value());
                methodBuilder.addStatement("$N = ($T) $N.getParcelable($S)", parameterName, variableElement.asType(), bundleCallStr, parameterAnno.value());
                methodBuilder.addStatement("isHaveValue = true");
                methodBuilder.endControlFlow();
            }
            if (mTypes.isSubtype(variableElement.asType(), serializableTypeMirror)) {
                methodBuilder.beginControlFlow("if (!isHaveValue && $N.containsKey($S) && $N.getSerializable($S) != null) ", bundleCallStr, parameterAnno.value(), bundleCallStr, parameterAnno.value());
                methodBuilder.addStatement("$N = ($T) $N.getSerializable($S)", parameterName, variableElement.asType(), bundleCallStr, parameterAnno.value());
                methodBuilder.endControlFlow();
            }

        }
    }

}
