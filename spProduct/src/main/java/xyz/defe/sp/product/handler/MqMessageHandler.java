package xyz.defe.sp.product.handler;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
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
    //process product quantity deduction request from ORDER SERVICE
    @SendTo(Const.QUEUE_DEDUCT_QUANTITY_REPLY)
    @RabbitListener(queuesToDeclare = @Queue(Const.QUEUE_DEDUCT_QUANTITY_REQUEST), concurrency = "5-20")  //it will create queue if not exists
    public DeductionResult deductQuantityHandle(OrderMsg msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.debug("got message(deduct product quantity) from ORDER SERVICE,OrderMsg id={}", msg.getId());

        DeductionResult result = new DeductionResult(msg.getOrderId(), "");
        if (msg == null) {result.setMessage("OrderMsg is null, msg id="+msg.getId());}

        //if not add catch block,when exception throw out,exception info will keep print
        //because the message is not be consumed and MQ server will keep resend message
        try {
            quantityService.checkAndDeduct(msg.getOrderId(), msg.getCounterMap());
            channel.basicAck(tag, false);
            result.setSuccessful(true);
        } catch (Exception e) {
            //drop message
            channel.basicAck(tag, false);
            result.setMessage(e.getMessage());
            log.error("consume message failed,drop message,OrderMsg id={},{}", msg.getId(), e.getMessage());
            e.printStackTrace();
        }
        log.debug("replay to ORDER SERVICE,result={}", result.isSuccessful());
        return result;
    }
}
