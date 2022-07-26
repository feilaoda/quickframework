package com.quickpaas.framework.utils;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameUtils {
    public static String toLowerUnderScoreName(String column) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column) ;
    }

    public static String firstToLowerCase(String param) {
        return isBlank(param) ? "" : param.substring(0, 1).toLowerCase() + param.substring(1);
    }
    public static String firstToUpperCase(String param) {
        return isBlank(param) ? "" : param.substring(0, 1).toUpperCase() + param.substring(1);
    }

    public static String toColumnName(String column) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column) ;
    }



    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int length = cs.length();

            for(int i = 0; i < length; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }

        return true;
    }
}
