package xyz.defe.sp.common.exception;

public class ExceptionUtil {

    public static void warn(String message) {
        throw new WarnException(message);
    }

}
