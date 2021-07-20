package xyz.defe.sp.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.ExceptionUtil;
import xyz.defe.sp.common.RestUtil;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.ResponseData;

@Service
public class OrderService {
    @Value("${sp-order-service.url}")
    public String baseURL;
    @Autowired
    private RestTemplate rest;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * maybe the order is processing(deduct quantity) then retry
     * if deduct quantity successful, paymentState=1
     * order's paymentState:{0:init state, 1:to pay, 2:paid}
     * @param id
     * @return
     */
    public SpOrder getToPayOrder(String id) throws Exception {
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
            Thread.sleep(millsArr[index]);
            order = getOrder(id);
            index++;
        }
        log.warn("can not get the order in getToPayOrder(),services are busy,try it later");
        return null;
    }
    
    public SpOrder getOrder(String id) {
        ResponseData responseData = RestUtil.INSTANCE.set(rest)
                .get(baseURL + "/order/" + id, new ParameterizedTypeReference<ResponseData<SpOrder>>(){});
        SpOrder order = (SpOrder) responseData.getData();
        return order;
    }
}
