package xyz.defe.sp.common;

public class Const {
    public static final String PRODUCT_SERVER = "product_server";
    public static final String USER_SERVER = "user_server";
    public static final String ORDER_SERVER = "order_server";
    public static final String PAYMENT_SERVER = "payment_server";

    public static final String EXCHANGE_ORDER = "order.exchange";

    public static final String QUEUE_DEDUCT_QUANTITY_REQUEST = "orderProcess.deductQuantity.request.queue";
    public static final String QUEUE_DEDUCT_QUANTITY_REPLY = "orderProcess.deductQuantity.reply.queue";
    public static final String QUEUE_SET_ORDER_PAID = "orderProcess.setOrderPaid.queue";

    public static final String ROUTING_KEY_DEDUCT_QUANTITY_REQUEST = "orderProcess.deductQuantity.request";
    public static final String ROUTING_KEY_DEDUCT_QUANTITY_REPLY = "orderProcess.deductQuantity.reply";

    public static final String LOCK_KEY_PRODUCT_GLOBAL = "productGlobalLock";
    public static final String LOCK_KEY_PRODUCT_PREFIX = "lockForProduct-";
    public static final String LOCK_KEY_ORDER_TOKEN_PREFIX = "orderTokenLock-";
    public static final String LK_ORDER_DEDUCT_QUANTITY_PRE = "orderDeductQuantityLock-";
    public static final String LK_ORDER_RESTORE_QUANTITY_PRE = "orderRestoreQuantityLock-";
    public static final String USER_LOCK_FOR_PAYMENT_PRE = "userLockForPayment-";

    public static final String OPERATION_SET_ORDER_PAID = "setOrderPaid";

    public static final long EXPIRED_TIME_ORDER_MILLIS = 1800000L;  //30 minutes

    public static final String TOKEN_ORDER_PREFIX = "orderToken-";
}
