package com.ehi.component;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class ComponentUtil {

    /**
     * 当使用 Fragment 或者 Context 中有这个 tag 的 fragment,那么就会最终用这个实现跳转
     */
    public static final String FRAGMENT_TAG = "EHiRxFragment";

    /**
     * 1.这是注解驱动器生成类的时候的目录
     * 2.这也是一些写好的实现类的包名字,不要轻易更改,如果要更改,请仔细比对
     * ComponentApiImpl 模块的实现类的位置
     */
    public static final String IMPL_OUTPUT_PKG = "com.ehi.component.impl";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 生成的文件名称的后缀
     */
    public static final String UIROUTER = "UiRouter";

    /**
     * 生成的文件名称的后缀
     */
    public static final String MODULE_APPLCATION = "ModuleApplication";

    /**
     * 路由框架基础实现类的全类名
     */
    public static final String UIROUTER_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "EHiModuleRouterImpl";

    /**
     * 生命周期框架基础实现类的全类名
     */
    public static final String MODULE_APPLICATION_IMPL_CLASS_NAME = IMPL_OUTPUT_PKG + DOT + "EHiModuleApplicationImpl";

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

    public static String genHostUIRouterClassName(String host) {
        String claName = IMPL_OUTPUT_PKG + DOT + firstCharUpperCase(host) + UIROUTER;
        return new String(claName);
    }

    public static String genHostModuleApplicationClassName(String host) {
        String claName = IMPL_OUTPUT_PKG + DOT + firstCharUpperCase(host) + MODULE_APPLCATION;
        return new String(claName);
    }

}
