package com.github.liaoxiangyun.ideaplugin.template.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface CodeGenerateService {

    /**
     * 获取实例对象
     *
     * @param project 项目对象
     * @return 实例对象
     */
    static CodeGenerateService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, CodeGenerateService.class);
    }

    /**
     * 生成代码
     *
     * @param template 模板
     * @return 生成好的代码
     */
    String generate(String template);
}
