package com.ehi.base;

/**
 * 组件化的业务组件配置中心
 * time   : 2018/11/27
 *
 * @author : xiaojinzi 30212
 */
public class ModuleConfig {

    public static class System {

        public static final String NAME = "system";

        // 测试跳转到打电话的界面
        public static final String CALL_PHONE = "callPhone";

        // 测试跳转到 APP 详情界面
        public static final String SYSTEM_APP_DETAIL = "appDetail";

    }

    public static class App {

        public static final String NAME = "app";

        // 测试跳转的界面
        public static final String TEST_ROUTER = "testRouter";
        // 测试代码质量的界面
        public static final String TEST_QUALITY = "testQuality";
        public static final String TEST_FRAGMENT_ROUTER = "testFragmentRouter";
        // 测试服务的界面
        public static final String TEST_SERVICE = "testService";
        // 测试错误页面
        public static final String TEST_ERROR = "testError";
        // 显示一个信息的界面
        public static final String INFO = "info";

        public static final String NOT_FOUND_TEST = "notFountTest";
    }

    public static class Module1 {

        public static final String NAME = "component1";
        public static final String TEST = "test";
        public static final String TEST_QUERY = "testQuery";
        public static final String TEST_LOGIN = "testLogin";
        public static final String TEST_DIALOG = "testDialog";

    }

    public static class Module2 {

        public static final String NAME = "component2";
        public static final String MAIN = "main";

    }

    public static class User {

        public static final String NAME = "user";
        public static final String LOGIN = "login";

    }

    public static class Help {
        public static final String NAME = "help";
    }

}
