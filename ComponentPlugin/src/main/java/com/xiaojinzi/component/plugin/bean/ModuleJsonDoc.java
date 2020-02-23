package com.xiaojinzi.component.plugin.bean;

import java.util.List;

public class ModuleJsonDoc {

    private String host;

    private String path;

    private String desc;

    private String targetActivityName;

    private String targetActivity;

    private String targetSimpleMethod;

    private String targetMethod;

    private List<ActivityAttrDoc> activityAttrDocs;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTargetActivityName() {
        return targetActivityName;
    }

    public void setTargetActivityName(String targetActivityName) {
        this.targetActivityName = targetActivityName;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    public String getTargetSimpleMethod() {
        return targetSimpleMethod;
    }

    public void setTargetSimpleMethod(String targetSimpleMethod) {
        this.targetSimpleMethod = targetSimpleMethod;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public List<ActivityAttrDoc> getActivityAttrDocs() {
        return activityAttrDocs;
    }

    public void setActivityAttrDocs(List<ActivityAttrDoc> activityAttrDocs) {
        this.activityAttrDocs = activityAttrDocs;
    }

}
