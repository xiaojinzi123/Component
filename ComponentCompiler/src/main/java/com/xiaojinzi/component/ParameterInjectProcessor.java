package com.xiaojinzi.component;

import com.google.auto.service.AutoService;
import com.xiaojinzi.component.anno.GlobalInterceptorAnno;
import com.xiaojinzi.component.anno.InterceptorAnno;
import com.xiaojinzi.component.anno.ParameterAnno;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 针对指定注解起作用
@SupportedAnnotationTypes({ComponentUtil.PARAMETERANNO_CLASS_NAME})
public class ParameterInjectProcessor extends BaseProcessor {

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
        mMessager.printMessage(Diagnostic.Kind.NOTE, "map = " + map);
    }

}
