package xyz.defe.sp.common;

public class ExceptionUtil {

    public static void warn(String message) throws WarnException {
        throw new WarnException(message);
    }
}
