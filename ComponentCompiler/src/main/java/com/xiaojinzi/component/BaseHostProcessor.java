package com.xiaojinzi.component;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * 获取 HOST 属性
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
public abstract class BaseHostProcessor extends BaseProcessor {

    // 在每一个 module 中配置的 HOST 的信息
    protected String componentHost = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            componentHost = options.get("HOST");
        }
        if (componentHost == null || componentHost.isEmpty()) {
            throw NULLHOSTEXCEPTION;
        }
        /*boolean isMatch = componentHost.matches(ComponentConstants.HOST_REGEX);
        if (!isMatch) {
            throw new RuntimeException("the host is not a valid name：" + componentHost);
        }*/
    }

}
