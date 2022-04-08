package com.github.liaoxiangyun.ideaplugin.java.constant

import java.util.regex.Pattern

class Constant {

    companion object {
        const val EW_CLASS = "com.szewec.framework.mybatisplus.mapper.Wrapper"

        const val IGNORE_NAME: String = "com.szewec.framework.mybatisplus.annotations.TableField"
        const val SWAGGER_NAME: String = "io.swagger.annotations.ApiModelProperty"
        val SWAGGER_P1: Pattern = Pattern.compile("\\(\"(.*?)\"\\)")
        val SWAGGER_P2: Pattern = Pattern.compile("value = \"(.*?)\"")
    }


}