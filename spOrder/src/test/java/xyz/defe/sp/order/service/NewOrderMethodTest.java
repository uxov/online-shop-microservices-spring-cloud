package xyz.defe.sp.order.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.order.dao.OrderDao;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewOrderMethodTest {
    @Mock
    private Gson gson;
    @Mock
    private OrderDao orderDao;
    @Mock
    private ProductService productService;
    @Mock
    private LocalMessageService localMessageService;
    @Mock
    private RedissonClient redisson;
    @Mock
    private RBucket<Object> bucket;
    @Spy
    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private SpOrder order;
    private Set<Product> productSet;

    @BeforeEach
    void setUp() {
        Product product1 = new Product();
        product1.setId("product1");
        Product product2 = new Product();
        product2.setId("product2");
        productSet = Set.of(product1, product2);

        cart = new Cart();
        cart.setUid("user123");
        cart.setOrderToken("token123");
        cart.setCounterMap(Map.of("product1", 2, "product2", 1));

        order = new SpOrder(cart.getUid(), gson.toJson(cart));
    }

    @Test
    @DisplayName("Successfully create a new order with valid cart")
    void successfullyCreateNewOrderWithValidCart() {

        // Mock redisson.getBucket().setIfAbsent()
        when(redisson.getBucket(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken())).thenReturn(bucket);
        when(bucket.setIfAbsent(1, Duration.ofMinutes(30))).thenReturn(true);

        // Mock productService.getProducts()
        when(productService.getProducts(cart.getCounterMap().keySet())).thenReturn(List.copyOf(productSet));

        // Mock orderDao.saveAndFlush()
        when(orderDao.saveAndFlush(Mockito.<SpOrder>any())).thenReturn(order);

        // Mock localMessageService.saveOrderMessage()
        doNothing().when(localMessageService).saveOrderMessage(Mockito.<OrderMsg>any());

        doNothing().when(orderService).sendOrderMsg(Mockito.<OrderMsg>any(), anyBoolean());

        // Execute the method
        SpOrder result = orderService.newOrder(cart);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("user123", result.getUserId());

        // Verify interactions
        verify(redisson).getBucket(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken());
        verify(bucket).setIfAbsent(1, Duration.ofMinutes(30));
        verify(productService).getProducts(cart.getCounterMap().keySet());
        verify(orderDao).saveAndFlush(Mockito.<SpOrder>any());
        verify(localMessageService).saveOrderMessage(Mockito.<OrderMsg>any());
        verify(orderService).sendOrderMsg(Mockito.<OrderMsg>any(), anyBoolean());
    }

    @Test
    void testNewOrderCartCheckFailed() {
        // Mock redisson.getBucket().setIfAbsent()
        when(redisson.getBucket(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken())).thenReturn(bucket);
        when(bucket.setIfAbsent(1, Duration.ofMinutes(30))).thenReturn(false);

        // Execute the method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> orderService.newOrder(cart));

        // Verify interactions
        verify(redisson).getBucket(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken());
        verify(bucket).setIfAbsent(1, Duration.ofMinutes(30));
        assertEquals("duplicate submission", exception.getMessage());
        Mockito.verifyNoInteractions(orderDao, localMessageService);
    }

}
