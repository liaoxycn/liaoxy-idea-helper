package com.github.liaoxiangyun.ideaplugin.common.util

import org.apache.commons.lang3.ArrayUtils
import java.util.*
import java.util.stream.Collectors

/**
 * The type String utils.
 */
object StringUtils {
    /**
     * Upper case first char string.
     *
     * @param str the str
     * @return the string
     */
    fun upperCaseFirstChar(str: String?): String? {
        return if (str == null) {
            null
        } else {
            if (str.isEmpty()) str else str.substring(0, 1).toUpperCase() + str.substring(1)
        }
    }

    /**
     * Lower case first char string.
     *
     * @param str the str
     * @return the string
     */
    fun lowerCaseFirstChar(str: String?): String? {
        return if (str == null) {
            null
        } else {
            if (str.isEmpty()) str else str.substring(0, 1).toLowerCase() + str.substring(1)
        }
    }

    /**
     * convert string from slash style to camel style, such as my_course will convert to MyCourse
     *
     * @param str the str
     * @return string
     */
    fun dbStringToCamelStyle(str: String?): String? {
        var str = str
        if (str != null) {
            str = str.toLowerCase()
            val sb = StringBuilder()
            sb.append(str[0].toString().toUpperCase())
            var i = 1
            while (i < str.length) {
                val c = str[i]
                if (c != '_') {
                    sb.append(c)
                } else {
                    if (i + 1 < str.length) {
                        sb.append(str[i + 1].toString().toUpperCase())
                        i++
                    }
                }
                i++
            }
            return sb.toString()
        }
        return null
    }

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    fun isEmpty(str: Any?): Boolean {
        return str == null || "" == str
    }

    /**
     * 驼峰转下划线
     * @param camelStr
     * @return
     */
    fun camelToSlash(camelStr: String?): String {
        val strings = splitByCharacterType(camelStr, true)
        return Arrays.stream(strings).map { obj: String? -> lowerCaseFirstChar(obj) }.collect(Collectors.joining("_"))
    }

    private fun splitByCharacterType(str: String?, camelCase: Boolean): Array<String?>? {
        return if (str == null) {
            null
        } else if (str.isEmpty()) {
            ArrayUtils.EMPTY_STRING_ARRAY
        } else {
            val c = str.toCharArray()
            val list: MutableList<String?> = arrayListOf()
            var tokenStart = 0
            var currentType = Character.getType(c[tokenStart])
            for (pos in tokenStart + 1 until c.size) {
                val type = Character.getType(c[pos])
                if (type != currentType) {
                    if (camelCase && type == 2 && currentType == 1) {
                        val newTokenStart = pos - 1
                        if (newTokenStart != tokenStart) {
                            list.add(String(c, tokenStart, newTokenStart - tokenStart))
                            tokenStart = newTokenStart
                        }
                    } else {
                        list.add(String(c, tokenStart, pos - tokenStart))
                        tokenStart = pos
                    }
                    currentType = type
                }
            }
            list.add(String(c, tokenStart, c.size - tokenStart))
            list.toTypedArray()
        }
    }
}