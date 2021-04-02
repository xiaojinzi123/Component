package com.xiaojinzi.component;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
public class ComponentUtil {

    private ComponentUtil() {
    }

    /**
     * 1.这是注解驱动器生成类的时候的目录
     * 2.这也是一些写好的实现类的包名字,不要轻易更改,如果要更改,请仔细比对
     * ComponentApiImpl 模块的实现类的位置
     */
    public static final String IMPL_OUTPUT_PKG = "com.xiaojinzi.component.impl";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 生成的文件名称的后缀
     */
    public static final String UIROUTER = "RouterGenerated";

    /**
     * 生成的降级文件名称的后缀
     */
    public static final String UIROUTER_DEGRADE = "RouterDegradeGenerated";

    /**
     * 生成的路由接口的实现类的后缀
     */
    public static final String UIROUTERAPI = "RouterApiGenerated";

    /**
     * 生成的文件名称的后缀
     */
    public static final String MODULE_APPLCATION = "ModuleAppGenerated";

    /**
     * 生成的文件名称的后缀
     */
    public static final String MODULE_APPLCATION_DEFAULT = "ModuleAppGeneratedDefault";

    /**
     * 生成的文件名称的后缀
     */
    public static final String SERVICE = "ServiceGenerated";

    /**
     * 生成的文件名称的后缀
     */
    public static final String INTERCEPTOR = "InterceptorGenerated";

    /**
     * 生成的文件名称的后缀
     */
    public static final String Fragment = "FragmentGenerated";

    public static final String GLOBAL_INTERCEPTOR_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.GlobalInterceptorAnno";
    public static final String INTERCEPTOR_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.InterceptorAnno";
    public static final String ROUTER_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.RouterAnno";
    public static final String FRAGMENTANNO_CLASS_NAME = "com.xiaojinzi.component.anno.FragmentAnno";
    public static final String ROUTER_DEGRADE_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.RouterDegradeAnno";
    public static final String MODULE_APP_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ModuleAppAnno";
    public static final String SERVICE_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ServiceAnno";
    public static final String SERVICE_DECORATOR_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ServiceDecoratorAnno";
    public static final String PARAMETERANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ParameterAnno";
    public static final String ATTR_VALUE_AUTOWIREDANNO_CLASS_NAME = "com.xiaojinzi.component.anno.AttrValueAutowiredAnno";
    public static final String SERVICEAUTOWIREDANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ServiceAutowiredAnno";
    public static final String URIAUTOWIREDANNO_CLASS_NAME = "com.xiaojinzi.component.anno.UriAutowiredAnno";
    public static final String ROUTERAPIANNO_CLASS_NAME = "com.xiaojinzi.component.anno.router.RouterApiAnno";


    // 路由框架基础实现类的全类名

    public static final String UIROUTER_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "ModuleRouterImpl";
    public static final String UIROUTER_DEGRADE_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "ModuleRouterDegradeImpl";
    public static final String MODULE_APPLICATION_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "application" + DOT + "ModuleApplicationImpl";
    public static final String SERVICE_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "service" + DOT + "ModuleServiceImpl";
    public static final String INTERCEPTOR_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "interceptor" + DOT + "ModuleInterceptorImpl";
    public static final String FRAGMENT_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "fragment" + DOT + "ModuleFragmentImpl";

    /**
     * 首字母小写
     */
    /*public static String firstCharUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }*/
    public static char charUpperCase(char target) {
        if (target >= 'a' && target <= 'z') {
            target = (char) (target - 32);
        }
        return target;
    }

    /**
     * 转化 host 为一个合理的 Class name
     */
    public static String transformHostForClass(String host) {
        StringBuffer sb = new StringBuffer();
        char[] hostChars = host.toCharArray();
        for (int i = 0; i < hostChars.length; i++) {
            char itemChar = hostChars[i];
            // 是否是字母
            boolean isLetter = (itemChar >= 'a' && itemChar <= 'z') ||
                    (itemChar >= 'A' && itemChar <= 'Z');
            // 是否是数字
            boolean isNumber = itemChar >= '0' && itemChar <= '9';
            // 是否转化为 _
            boolean isTransform = false;
            boolean needUpperCase = false;
            if (i == 0) {
                if (isLetter) {
                    // 是字母需要转化为大写
                    needUpperCase = true;
                } else {
                    // 第一位如果不是字母, 一定要转化
                    isTransform = true;
                }
            } else {
                // 如果不是字母也不是数字
                if (!isLetter && !isNumber) {
                    isTransform = true;
                }
            }
            if (isTransform) {
                sb.append("_");
            } else {
                if (needUpperCase) {
                    sb.append(charUpperCase(itemChar));
                } else {
                    sb.append(itemChar);
                }
            }
        }
        return sb.toString();
    }

    public static String genRouterApiImplClassName(Class apiClass) {
        return apiClass.getName() + UIROUTERAPI;
    }

    public static String genHostModuleApplicationClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "application" + DOT + transformHostForClass(host) + MODULE_APPLCATION;
    }

    public static String genDefaultHostModuleApplicationClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "application" + DOT + transformHostForClass(host) + MODULE_APPLCATION_DEFAULT;
    }

    public static String genHostRouterClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + transformHostForClass(host) + UIROUTER;
    }

    public static String genHostRouterDegradeClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + transformHostForClass(host) + UIROUTER_DEGRADE;
    }

    public static String genHostServiceClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "service" + DOT + transformHostForClass(host) + SERVICE;
    }

    public static String genHostInterceptorClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "interceptor" + DOT + transformHostForClass(host) + INTERCEPTOR;
    }

    public static String genHostFragmentClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "fragment" + DOT + transformHostForClass(host) + Fragment;
    }

    public static String getGetSetMethodName(String name, boolean isGet, boolean isBoolean) {
        StringBuffer sb = new StringBuffer();
        char firstChar = charUpperCase(name.charAt(0));

        if (isBoolean && name.startsWith("is")) {
            if (isGet) {
                sb.append("is");
            } else {
                sb.append("set");
            }
            sb.append(name.substring(2));
        } else {
            if (isGet) {
                sb.append("get");
            } else {
                sb.append("set");
            }
            sb.append(firstChar);
            if (name.length() > 1) {
                sb.append(name.substring(1));
            }
        }
        return sb.toString();
    }

}
