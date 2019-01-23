package com.ehi.component;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * 获取 HOST 属性
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
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

        if (componentHost == null || "".equals(componentHost)) {
            ErrorPrintUtil.printHostNull(mMessager);
            return;
        }

    }

}
