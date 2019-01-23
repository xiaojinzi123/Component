package com.ehi.component;

import com.squareup.javapoet.ClassName;

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
 * @author : xiaojinzi 30212
 */
public abstract class BaseProcessor extends AbstractProcessor {

    protected Filer mFiler;
    protected Messager mMessager;
    protected Types mTypes;
    protected Elements mElements;

    protected TypeElement mTypeElementString;
    protected TypeElement mTypeElementList;
    protected TypeElement mTypeElementArrayList;

    protected ClassName mClassNameString;
    protected ClassName mClassNameList;
    protected ClassName mClassNameArrayList;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mFiler = processingEnv.getFiler();
        mMessager = processingEnvironment.getMessager();
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();

        mTypeElementString = mElements.getTypeElement(ComponentConstants.JAVA_STRING);
        mTypeElementList = mElements.getTypeElement(ComponentConstants.JAVA_LIST);
        mTypeElementArrayList = mElements.getTypeElement(ComponentConstants.JAVA_ARRAYLIST);

        mClassNameString = ClassName.get(mTypeElementString);
        mClassNameList = ClassName.get(mTypeElementList);
        mClassNameArrayList = ClassName.get(mTypeElementArrayList);

    }

}
