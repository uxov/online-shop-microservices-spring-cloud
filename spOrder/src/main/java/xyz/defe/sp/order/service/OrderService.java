package xyz.defe.sp.order.service;

import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.common.pojo.PageQuery;

import java.util.List;

public interface OrderService {
    String getOrderToken();

    SpOrder newOrder(Cart cart);

    SpOrder getOrder(String id);

    List<SpOrder> getValidOrders(String uid, PageQuery pageQuery);

    List<SpOrder> getUnpaidOrders(String uid);

    List<SpOrder> getUnpaidOrders();

    /**
     *
     * @param state {true:valid, false:invalid}
     */
    void setOrderState(String id, boolean state);

    /**
     *
     * @param state {0:init state, 1:to pay, 2:paid}
     */
    void setOrderPaymentState(String id, int state);

    SpOrder getPaidOrder(String id);

    SpOrder getToPayOrder(String id);

    void saveAll(Iterable<SpOrder> entities);

    void processExpiredOrders();

    void sendOrderMsg(OrderMsg msg, boolean isResend);
}
