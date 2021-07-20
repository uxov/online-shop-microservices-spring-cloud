package xyz.defe.sp.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.pojo.OrderMsg;

@Service
public class MqMessageService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private LocalMessageService localMessageService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //OrderMsg has the same id as LocalMessage
    public void sendOrderMsg(OrderMsg msg, int retry) {
        //RabbitMQ RPC - Request/Reply Pattern - send product quantity deduction request to PRODUCT SERVICE
        asyncRabbitTemplate.convertSendAndReceive(Const.EXCHANGE_ORDER, Const.ROUTING_KEY_DEDUCT_QUANTITY_REQUEST, msg)
                .addCallback(new ListenableFutureCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        log.info("got product quantity deduction result from PRODUCT SERVICE,result={},order id={}", result, msg.getOrderId());
                        int flag = (int) result;
                        if (flag == 0) {    //if deduct quantity failed then set order invalid
                            orderService.setOrderState(msg.getOrderId(), false);
                            log.info("deduct quantity failed,set order invalid,order id={}", msg.getOrderId());
                        } else if (flag == 1) {
                            //set order paymentState=1(to pay)
                            orderService.setOrderPaymentState(msg.getOrderId(), 1);
                            log.info("deduct quantity successful,set paymentState=1(to pay),order id={}", msg.getOrderId());
                        }
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        localMessageService.setRetry(msg.getId(), retry);
                        log.error("send message to MQ failed,OrderMsg id={},{}", msg.getId(), ex.getMessage());
                        ex.printStackTrace();
                    }
                });
        log.info("send product quantity deduction request to PRODUCT SERVICE");
    }
}
