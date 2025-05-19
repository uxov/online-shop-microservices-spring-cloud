package xyz.defe.sp.order.handler;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.common.service.ErrorLogService;
import xyz.defe.sp.order.service.OrderService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqMessageHandler {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ErrorLogService errorLogService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //listen messages from PAYMENT SERVICE
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @RabbitListener(queuesToDeclare = @Queue(Const.QUEUE_SET_ORDER_PAID), concurrency = "10-50")
    public void setOrderPaidHandle(OrderMsg message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.debug("got message(set order paid) from PAYMENT SERVICE,OrderMsg id={}", message.getId());

        //set order paid
        orderService.setOrderPaymentState(message.getOrderId(), 2);
        log.info("set order paid,order id={}", message.getOrderId());
        channel.basicAck(tag, false);
    }

    @Recover
    public void setOrderPaidHandle(RuntimeException e, OrderMsg message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        Map data = new HashMap();
        data.put("OrderMsg", message);

        //write error log, or use Dead Letter Queue
        errorLogService.log(Const.OPERATION_SET_ORDER_PAID, e.getMessage(), data);
        String error = "consume message failed,stop retry,OrderMsg id={},{}";
        log.error(error, message.getId(), e.getMessage(), e);
        channel.basicAck(tag, false);
    }

}
