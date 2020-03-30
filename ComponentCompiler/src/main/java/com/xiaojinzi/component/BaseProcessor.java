package com.xiaojinzi.component;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 基础的注解驱动器,帮助获取一些常用的信息
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
public abstract class BaseProcessor extends AbstractProcessor {

    public static final String NORMALLINE = "---------------------------";

    public static final RuntimeException NULLHOSTEXCEPTION = new RuntimeException("the host must not be null,you must define host in build.gradle file,such as:\n\n" +
            "defaultConfig {\n" +
            "    minSdkVersion 14\n" +
            "    targetSdkVersion 27\n" +
            "    versionCode 1\n" +
            "    versionName \"1.0\"\n\n" +
            "    javaCompileOptions {\n" +
            "        annotationProcessorOptions {\n" +
            "            arguments = [HOST: \"user\"]\n" +
            "        }\n" +
            "    }\n" +
            "}\n  \n");

    protected String routerDocFolder = null;

    protected Filer mFiler;
    protected Messager mMessager;
    protected Types mTypes;
    protected Elements mElements;

    protected TypeElement mTypeElementComponentGeneratedAnno;
    protected TypeElement mTypeElementString;
    protected TypeElement mTypeElementInteger;
    protected TypeElement mTypeElementList;
    protected TypeElement mTypeElementArrayList;
    protected TypeElement mTypeElementSparseArray;
    protected TypeElement mTypeElementHashMap;
    protected TypeElement mTypeElementHashSet;

    protected ClassName mClassNameComponentGeneratedAnno;
    protected ClassName mClassNameString;
    protected ClassName mClassNameList;
    protected ClassName mClassNameArrayList;
    protected ClassName mClassNameSparseArray;
    protected ClassName mClassNameHashMap;
    protected ClassName mClassNameHashSet;
    protected ClassName mClassNameKeep;
    protected ClassName mClassNameNonNull;

    protected TypeName mTypeNameString;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            routerDocFolder = options.get("RouterDoc");
        }

        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();

        mTypeElementComponentGeneratedAnno = mElements.getTypeElement(ComponentConstants.COMPONENT_GENERATED_ANNO_CLASS_NAME);
        mClassNameComponentGeneratedAnno = ClassName.get(mTypeElementComponentGeneratedAnno);

        mTypeElementString = mElements.getTypeElement(ComponentConstants.JAVA_STRING);
        mTypeElementInteger = mElements.getTypeElement(ComponentConstants.JAVA_INTEGER);
        mTypeElementList = mElements.getTypeElement(ComponentConstants.JAVA_LIST);
        mTypeElementArrayList = mElements.getTypeElement(ComponentConstants.JAVA_ARRAYLIST);
        mTypeElementSparseArray = mElements.getTypeElement(ComponentConstants.ANDROID_SPARSEARRAY);
        mTypeElementHashMap = mElements.getTypeElement(ComponentConstants.JAVA_HASHMAP);
        mTypeElementHashSet = mElements.getTypeElement(ComponentConstants.JAVA_HASHSET);

        mClassNameString = ClassName.get(mTypeElementString);
        mClassNameList = ClassName.get(mTypeElementList);
        mClassNameArrayList = ClassName.get(mTypeElementArrayList);
        mClassNameSparseArray = ClassName.get(mTypeElementSparseArray);
        mClassNameHashMap = ClassName.get(mTypeElementHashMap);
        mClassNameHashSet = ClassName.get(mTypeElementHashSet);

        mTypeNameString = TypeName.get(mTypeElementString.asType());

        // androidx 和 非 androidx 的两个注解
        mClassNameKeep = ClassName.get(mElements.getTypeElement(ComponentConstants.ANDROID_ANNOTATION_KEEP));
        mClassNameNonNull = ClassName.get(mElements.getTypeElement(ComponentConstants.ANDROID_ANNOTATION_NONNULL));

        if (mClassNameKeep == null || mClassNameNonNull == null) {
            throw new ProcessException("Your configuration is wrong. " +
                    "If you use androidx, see https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE-AndroidX " +
                    "\n else see https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE");
        }

    }



}
