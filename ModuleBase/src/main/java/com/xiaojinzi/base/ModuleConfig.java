package com.xiaojinzi.base;

/**
 * 组件化的业务组件配置中心
 * time   : 2018/11/27
 *
 * @author : xiaojinzi
 */
public class ModuleConfig {

    /**
     * 项目的 Scheme,用于跳转项目中的界面
     */
    public static final String APP_SCHEME = "Router";
    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";

    public static class System {

        public static final String NAME = "system";

        // 测试跳转到打电话的界面
        public static final String CALL_PHONE = "callPhone";

        // 拍照
        public static final String TAKE_PHONE = "takePhone";

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
        public static final String TEST_AUTORETURN = "testAutoReturn";
        public static final String TEST_AUTORETURN1 = "testAutoReturn1";
        public static final String TEST_QUERY = "testQuery";
        public static final String TEST_LOGIN = "testLogin";
        public static final String TEST_DIALOG = "testDialog";
        public static final String TEST_IN_OTHER_MODULE = "testInOtherModule";
        public static final String TEST_INJECT1 = "testInject1";
        public static final String TEST_INJECT2 = "testInject2";
        public static final String TEST_INJECT3 = "testInject3";
        public static final String TEST_INJECT4 = "testInject4";
        public static final String TEST_PUT_QUERY_WITH_URL = "testPutQueryWithUrl";
        public static final String TEST_FRAGMENT = "component1.fragment";

    }

    public static class Module2 {

        public static final String NAME = "component2";
        public static final String MAIN = "main";

    }

    public static class User {

        public static final String NAME = "user";
        public static final String LOGIN = "login";
        public static final String LOGIN_URL = NAME + "/" + LOGIN;
        public static final String PERSON_CENTER = "personCenter";
        public static final String PERSON_CENTER_FOR_TEST = "personCenterForTest";

    }

    public static class Help {
        public static final String NAME = "www.xiaojinzi.help";
        public static final String WEB = "web";
        public static final String TEST_WEB_ROUTER = "testWebRouter";
        public static final String CANCEL_FOR_TEST = "cancelForTest";
        public static final String SHOULD_NOT_APPEAR = "shouldNotAppear";
    }

    public static class Test {
        public static final String NAME = "test_";
    }

}
