package xyz.defe.sp.order.service;

import xyz.defe.sp.common.WarnException;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.PageQuery;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrderService {
    String getOrderToken();

    SpOrder newOrder(Cart cart) throws Exception;

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

    SpOrder getPaidOrder(String id) throws WarnException, InterruptedException, ExecutionException;

    void saveAll(Iterable<SpOrder> entities);
}
