package com.github.liaoxiangyun.ideaplugin.template.service;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerateServiceImpl implements CodeGenerateService {
    /**
     * 生成代码
     *
     * @param template 模板
     * @return 生成好的代码
     */
    @Override
    public String generate(String template) {
        return null;
    }


    /**
     * 获取默认参数
     *
     * @return 参数
     */
    private Map<String, Object> getDefaultParam() {
        // 系统设置
        Map<String, Object> param = new HashMap<>(20);

        return param;
    }

    public static void main(String[] args) {
        String user = System.getProperty("user.name");
        System.out.println("user = " + user);
    }
}
