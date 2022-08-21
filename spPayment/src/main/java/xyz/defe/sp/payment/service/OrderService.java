package xyz.defe.sp.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.exception.ExceptionUtil;

@Service
public class OrderService {
    @Autowired
    private OrderFeignClient orderFeignClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * maybe the order is processing(deduct quantity) then retry
     * if deduct quantity successful, paymentState=1
     * order's paymentState:{0:init state, 1:to pay, 2:paid}
     * @param id
     * @return
     */
    public SpOrder getToPayOrder(String id) {
        int index = 0;
        int paymentState = 1;
        int[] millsArr = new int[]{1000, 3000, 5000, 7000, 9000};

        SpOrder order = getOrder(id);
        if (order == null) {
            ExceptionUtil.warn("the order does not exists");
        }
        while (index < millsArr.length) {
            if (order != null && order.isValid() && order.getPaymentState() == paymentState) {
                log.info("got the order");
                return order;
            }
            log.info("getting the order...");
            try {
                Thread.sleep(millsArr[index]);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
                e.printStackTrace();
            }
            order = getOrder(id);
            index++;
        }
        log.warn("can not get the order in getToPayOrder(),services are busy,try it later");
        return null;
    }
    
    public SpOrder getOrder(String id) {
        return orderFeignClient.getOrder(id).getData();
    }
}
