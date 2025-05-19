package xyz.defe.sp.product.handler;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import xyz.defe.sp.common.pojo.DeductionResult;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.product.service.QuantityService;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MqMessageHandlerTest {
    @InjectMocks
    private MqMessageHandler mqMessageHandler;
    @Mock
    private QuantityService quantityService;
    @Mock
    private Channel channel;
    @Captor
    private ArgumentCaptor<Long> tagCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeductQuantityHandleSuccess() throws Exception {
        OrderMsg orderMsg = mock(OrderMsg.class);
        when(orderMsg.getId()).thenReturn("msg-1");
        when(orderMsg.getOrderId()).thenReturn("order-123");
        Map<String, Integer> counterMap = new HashMap<>();
        when(orderMsg.getCounterMap()).thenReturn(counterMap);

        long tag = 42L;

        OrderMsg resultMsg = mqMessageHandler.deductQuantityHandle(orderMsg, channel, tag);
        DeductionResult result = resultMsg.getDeductionResult();

        verify(quantityService).checkAndDeduct("order-123", counterMap);
        verify(channel).basicAck(tag, false);
        assertTrue(result.isSuccessful());
        assertEquals("order-123", result.getOrderId());
        assertNull(result.getMessage());
    }

    @Test
    void testDeductQuantityHandleException() throws Exception {
        OrderMsg orderMsg = mock(OrderMsg.class);
        when(orderMsg.getId()).thenReturn("msg-2");
        when(orderMsg.getOrderId()).thenReturn("order-456");
        Map<String, Integer> counterMap = new HashMap<>();
        when(orderMsg.getCounterMap()).thenReturn(counterMap);

        long tag = 99L;

        doThrow(new RuntimeException("deduction failed")).when(quantityService).checkAndDeduct(anyString(), anyMap());

        OrderMsg resultMsg = mqMessageHandler.deductQuantityHandle(orderMsg, channel, tag);
        DeductionResult result = resultMsg.getDeductionResult();

        verify(quantityService).checkAndDeduct("order-456", counterMap);
        verify(channel).basicAck(tag, false);
        assertFalse(result.isSuccessful());
        assertEquals("order-456", result.getOrderId());
        assertEquals("deduction failed", result.getMessage());
    }
}