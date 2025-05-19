package xyz.defe.sp.product.handler;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.pojo.DeductionResult;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.product.service.QuantityService;

import java.io.IOException;

@Service
public class MqMessageHandler {
    @Autowired
    private QuantityService quantityService;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    //listen messages from ORDER SERVICE
    //RabbitMQ RPC - Request/Reply Pattern
    //process product quantity deduction request for ORDER SERVICE
    //`queuesToDeclare` will create queue if not exists
    @RabbitListener(queuesToDeclare = @Queue(Const.QUEUE_DEDUCT_QUANTITY_REQUEST), concurrency = "10-50")
    public OrderMsg deductQuantityHandle(OrderMsg msg, Channel channel,
                                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        OrderMsg resultMsg = new OrderMsg();
        DeductionResult result = new DeductionResult();
        //if not add catch block,when exception throw out,exception info will keep print
        //because the message is not be consumed and MQ server will keep resend message
        try {
            log.debug("got message(deduct product quantity) from ORDER SERVICE,OrderMsg id={}", msg.getId());

            result.setOrderId(msg.getOrderId());
            quantityService.checkAndDeduct(msg.getOrderId(), msg.getCounterMap());
            result.setSuccessful(true);

            resultMsg.setId(msg.getId());
            resultMsg.setFrom(Const.PRODUCT_SERVER);
            resultMsg.setTo(Const.ORDER_SERVER);
            resultMsg.setRemark("deduct product quantity reply");

            channel.basicAck(tag, false);
        } catch (Exception e) {
            result.setMessage(e.getMessage());

            //drop message,the sender will resend the message
            channel.basicAck(tag, false);
            log.error("consume message failed,drop message,OrderMsg id={},{}", msg.getId(), e.getMessage(), e);
        }
        resultMsg.setDeductionResult(result);
        log.info("replay to ORDER SERVICE,result={}", result.isSuccessful());
        return resultMsg;
    }
}
