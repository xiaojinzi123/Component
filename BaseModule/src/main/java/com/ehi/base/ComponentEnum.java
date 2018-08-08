package com.ehi.base;

/**
 * 组件的常量类
 */
public enum ComponentEnum {

    App("main"),Component1("component1"),Component2("component2");

    private String moduleName;

    ComponentEnum(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
}
