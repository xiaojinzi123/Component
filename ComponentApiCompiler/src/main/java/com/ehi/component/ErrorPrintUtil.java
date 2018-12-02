package com.ehi.component;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * time   : 2018/11/27
 *
 * @author : xiaojinzi 30212
 */
public class ErrorPrintUtil {

    public static void printHostNull(Messager mMessager) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                "the host must not be null,you must define host in build.gradle file,such as:\n\n" +
                        "defaultConfig {\n" +
                        "    minSdkVersion 14\n" +
                        "    targetSdkVersion 27\n" +
                        "    versionCode 1\n" +
                        "    versionName \"1.0\"\n\n" +
                        "    javaCompileOptions {\n" +
                        "        annotationProcessorOptions {\n" +
                        "            arguments = [HOST: \"component2\"]\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n  \n"
        );
    }

}
