package com.xiaojinzi.component;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class ComponentUtil {

    private ComponentUtil() {
    }

    /**
     * 当使用 Fragment 或者 Context 中有这个 tag 的 fragment,那么就会最终用这个实现跳转
     */
    public static final String FRAGMENT_TAG = "RouterRxFragment";

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
     * 生成的文件名称的后缀
     */
    public static final String MODULE_APPLCATION = "ModuleApplicationGenerated";

    /**
     * 生成的文件名称的后缀
     */
    public static final String SERVICE = "ServiceGenerated";

    /**
     * 生成的文件名称的后缀
     */
    public static final String INTERCEPTOR = "InterceptorGenerated";

    public static final String GLOBAL_INTERCEPTOR_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.GlobalInterceptorAnno";
    public static final String INTERCEPTOR_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.InterceptorAnno";
    public static final String ROUTER_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.RouterAnno";
    public static final String MODULE_APP_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ModuleAppAnno";
    public static final String SERVICE_ANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ServiceAnno";
    public static final String PARAMETERANNO_CLASS_NAME = "com.xiaojinzi.component.anno.ParameterAnno";
    public static final String ROUTERAPIANNO_CLASS_NAME = "com.xiaojinzi.component.anno.router.RouterApiAnno";

    // 路由框架基础实现类的全类名

    public static final String UIROUTER_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "ModuleRouterImpl";
    public static final String MODULE_APPLICATION_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "application" + DOT + "ModuleApplicationImpl";
    public static final String SERVICE_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "service" + DOT + "MuduleServiceImpl";
    public static final String INTERCEPTOR_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "interceptor" + DOT + "MuduleInterceptorImpl";

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String firstCharUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static String genHostModuleApplicationClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "application" + DOT + firstCharUpperCase(host) + MODULE_APPLCATION;
    }

    public static String genHostRouterClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + firstCharUpperCase(host) + UIROUTER;
    }

    public static String genHostServiceClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "service" + DOT + firstCharUpperCase(host) + SERVICE;
    }

    public static String genHostInterceptorClassName(String host) {
        return IMPL_OUTPUT_PKG + DOT + "interceptor" + DOT + firstCharUpperCase(host) + INTERCEPTOR;
    }

}
