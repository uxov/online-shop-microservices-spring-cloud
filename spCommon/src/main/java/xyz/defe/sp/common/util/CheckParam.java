package xyz.defe.sp.common.util;

import xyz.defe.sp.common.exception.ExceptionUtil;

import java.util.Map;

public class CheckParam {
    public static void check(Object obj, String message) {
        if (obj instanceof String s) {
            if (null == s || s.equals("")) {
                ExceptionUtil.warn(message);
            }
        }
        if (obj instanceof Map m) {
            if (null == m || m.isEmpty()) {
                ExceptionUtil.warn(message);
            }
        }
    }
}
