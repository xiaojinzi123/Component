package com.ehi.base;

/**
 * 组件化的业务组件配置中心
 * time   : 2018/11/27
 *
 * @author : xiaojinzi 30212
 */
public class ModuleConfig {

    public static class App {

        public static final String NAME = "app";

        // 测试跳转的界面
        public static final String TEST_ROUTER = "testRouter";
        // 测试服务的界面
        public static final String TEST_SERVICE = "testService";

    }

    public static class Component1 {

        public static final String NAME = "component1";

    }

    public static class Component2 {

        public static final String NAME = "component2";

    }

    public static class User {

        public static final String NAME = "user";

    }

}
