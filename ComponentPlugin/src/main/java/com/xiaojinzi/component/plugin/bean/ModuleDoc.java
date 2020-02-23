package com.xiaojinzi.component.plugin.bean;

import java.util.List;

public class ModuleDoc {

    private String moduleName;

    private List<ModuleJsonDoc> data;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<ModuleJsonDoc> getData() {
        return data;
    }

    public void setData(List<ModuleJsonDoc> data) {
        this.data = data;
    }

}
