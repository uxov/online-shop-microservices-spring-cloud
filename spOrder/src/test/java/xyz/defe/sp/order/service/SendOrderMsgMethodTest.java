package xyz.defe.sp.order.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import xyz.defe.sp.common.enums.LocalMsgState;
import xyz.defe.sp.common.pojo.DeductionResult;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.order.dao.OrderDao;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SendOrderMsgMethodTest {
    @Mock
    private AsyncRabbitTemplate asyncRabbitTemplate;
    @Mock
    private OrderDao orderDao;
    @Mock
    private LocalMessageService localMessageService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private RabbitConverterFuture<Object> mockRabbitFuture(Object result, Throwable ex) {
        RabbitConverterFuture<Object> future = mock(RabbitConverterFuture.class);
        CompletableFuture<Object> cf = ex != null ?
                CompletableFuture.failedFuture(ex) :
                CompletableFuture.completedFuture(result);

        // bind CompletableFuture to RabbitConverterFuture
        when(future.whenComplete(any())).thenAnswer(inv -> {
            cf.whenComplete(inv.getArgument(0));
            return future;
        });
        return future;
    }

    @Test
    void whenDeductionSuccessThenSetPaymentState1() throws Exception {
        OrderMsg msg = new OrderMsg();
        msg.setOrderId("order-123");
        DeductionResult successResult = new DeductionResult();
        successResult.setSuccessful(true);
        OrderMsg resultMsg = new OrderMsg();
        resultMsg.setDeductionResult(successResult);

        RabbitConverterFuture<Object> future = mockRabbitFuture(resultMsg, null);

        ArgumentCaptor<OrderMsg> msgCaptor = ArgumentCaptor.forClass(OrderMsg.class);
        when(asyncRabbitTemplate.convertSendAndReceive(msgCaptor.capture())).thenReturn(future);

        orderService.sendOrderMsg(msg, false);
//        future.complete(resultMsg);

        assertEquals(msg, msgCaptor.getValue());
        verify(orderDao).setOrderPaymentState("order-123", 1);
        verify(localMessageService).setMessageState(msg.getId(), LocalMsgState.SENT);
    }

    @Test
    void whenDeductionFailedThenSetOrderInvalid() throws Exception {
        OrderMsg msg = new OrderMsg();
        msg.setOrderId("order-456");
        DeductionResult failedResult = new DeductionResult();
        failedResult.setSuccessful(false);
        failedResult.setMessage("Insufficient stock");
        OrderMsg resultMsg = new OrderMsg();
        resultMsg.setDeductionResult(failedResult);

        RabbitConverterFuture<Object> future = mockRabbitFuture(resultMsg, null);
        ArgumentCaptor<OrderMsg> msgCaptor = ArgumentCaptor.forClass(OrderMsg.class);
        when(asyncRabbitTemplate.convertSendAndReceive(msgCaptor.capture())).thenReturn(future);

        orderService.sendOrderMsg(msg, false);
//        future.complete(resultMsg);

        verify(orderDao).setOrderInvalid(eq("order-456"), anyString());
        verify(localMessageService).setMessageState(msg.getId(), LocalMsgState.SENT);
    }

    @Test
    void whenSendFailsAndNotResendThenSetPendingResend1() throws Exception {
        OrderMsg msg = new OrderMsg();
        msg.setOrderId("order-789");
        Throwable ex = new RuntimeException("message send failed");

        RabbitConverterFuture<Object> future = mockRabbitFuture(null, ex);
        ArgumentCaptor<OrderMsg> msgCaptor = ArgumentCaptor.forClass(OrderMsg.class);
        when(asyncRabbitTemplate.convertSendAndReceive(msgCaptor.capture())).thenReturn(future);

        orderService.sendOrderMsg(msg, false);
//        future.completeExceptionally(ex);

        verify(localMessageService).setMessageState(msg.getId(), LocalMsgState.PENDING_RESEND);
    }

    @Test
    void whenSendFailsAndIsResendThenSetExceptionState() throws Exception {
        OrderMsg msg = new OrderMsg();
        msg.setOrderId("order-012");
        Throwable ex = new RuntimeException("message send failed");

        RabbitConverterFuture<Object> future = mockRabbitFuture(null, ex);
        ArgumentCaptor<OrderMsg> msgCaptor = ArgumentCaptor.forClass(OrderMsg.class);
        when(asyncRabbitTemplate.convertSendAndReceive(msgCaptor.capture())).thenReturn(future);

        orderService.sendOrderMsg(msg, true); // isReSend=true
//        future.completeExceptionally(ex);

        verify(localMessageService).setMessageState(msg.getId(), LocalMsgState.EXCEPTION);
    }
}
