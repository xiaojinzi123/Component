package com.xiaojinzi.component;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class Utils {

    public static boolean isKotlinFile(TypeElement targetClass) {
        List<? extends AnnotationMirror> tempList = targetClass.getAnnotationMirrors();
        boolean isKotlinFile = false;
        for (AnnotationMirror annotationMirror : tempList) {
            if (ComponentConstants.KOTLIN_METADATA.equals(annotationMirror.getAnnotationType().toString())) {
                isKotlinFile = true;
                break;
            }
        }
        return isKotlinFile;
    }

    public static void generateCondition(Elements elements,
                                         TypeElement conditionCacheTypeElement,
                                         StringBuffer conditionsSB,
                                         List<Object> conditionsArgs,
                                         List<String> conditionsImplClassNames) {
        for (int i = 0; i < conditionsImplClassNames.size(); i++) {
            String conditionClassName = conditionsImplClassNames.get(i);
            if (i == 0) {
                conditionsSB.append("$T.getByClass($T.class).matches()");
            } else {
                conditionsSB.append(" && $T.getByClass($T.class).matches()");
            }
            conditionsArgs.add(conditionCacheTypeElement);
            conditionsArgs.add(elements.getTypeElement(conditionClassName));
        }
    }

}
