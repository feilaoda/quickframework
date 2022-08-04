package com.quickpaas.framework.utils;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class NameUtils {
    public static String toLowerUnderScoreName(String column) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column) ;
    }

    public static String firstToLowerCase(String param) {
        return StringUtils.isEmpty(param) ? "" : param.substring(0, 1).toLowerCase() + param.substring(1);
    }
    public static String firstToUpperCase(String param) {
        return StringUtils.isEmpty(param) ? "" : param.substring(0, 1).toUpperCase() + param.substring(1);
    }

    public static String toColumnName(String column) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column) ;
    }

}
