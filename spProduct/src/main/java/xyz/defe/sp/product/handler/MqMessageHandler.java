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
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.product.service.ProductService;

import java.io.IOException;

@Service
public class MqMessageHandler {
    @Autowired
    private ProductService productService;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    //listen messages from ORDER SERVICE
    //RabbitMQ RPC - Request/Reply Pattern - process product quantity deduction request from ORDER SERVICE
    @SendTo(Const.QUEUE_DEDUCT_QUANTITY_REPLY)
    @RabbitListener(queuesToDeclare = @Queue(Const.QUEUE_DEDUCT_QUANTITY_REQUEST))  //it will create queue if not exists
    public int deductQuantityHandle(OrderMsg msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        int result = 0;
        if (msg == null) {return result;}
        log.info("got message(deduct product quantity) from ORDER SERVICE,OrderMsg id={}", msg.getId());

        //if not add catch block,when exception throw out,exception info will keep print
        //because the message is not be consumed and MQ server will keep resend message
        try {
            result = productService.checkAndDeduct(msg.getOrderId(), msg.getCounterMap());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            //drop message
            channel.basicAck(tag, false);
            log.error("consume message failed,drop message,OrderMsg id={},{}", msg.getId(), e.getMessage());
            e.printStackTrace();
        }
        log.info("replay to ORDER SERVICE,result={}", result);
        return result;
    }
}
