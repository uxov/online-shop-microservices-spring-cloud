package xyz.defe.sp.payment.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.payment.dao.PaymentLogDao;
import xyz.defe.sp.payment.dao.WalletDao;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Spy
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private OrderService orderService;
    @Mock
    private WalletDao walletDao;
    @Mock
    private ProductService productService;
    @Mock
    private Gson gson;
    @Mock
    private LocalMessageService localMessageService;
    @Mock
    private MqMessageService mqMessageService;
    @Mock
    private PaymentLogDao paymentLogDao;
    @Mock
    private RedissonClient redisson;
    @Mock
    private RLock lock;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testCheckAndPayParamCheckFailed() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.checkAndPay("", "order123");
        });
        Assertions.assertTrue(exception.getMessage().contains("uid is null or empty"));

        exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.checkAndPay("user123", "");
        });
        Assertions.assertTrue(exception.getMessage().contains("orderId is null or empty"));
    }

    @Test
    public void testCheckAndPayOrderAlreadyPaid() {
        when(redisson.getLock(anyString())).thenReturn(lock);
        when(paymentLogDao.findByOrderId(anyString())).thenReturn(new PaymentLog());

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.checkAndPay("user123", "order123");
        });

        Assertions.assertTrue(exception.getMessage().contains("the order is paid"));
    }

    @Test
    public void testCheckAndPayOrderNotPaid() {
        PaymentLog paymentLog = new PaymentLog();
        when(redisson.getLock(anyString())).thenReturn(lock);
        when(paymentLogDao.findByOrderId(anyString())).thenReturn(null);
        doReturn(paymentLog).when(paymentService).pay(anyString());

        PaymentLog result = paymentService.checkAndPay("user123", "order123");

        assertNotNull(result);
        verify(lock, times(1)).lock(30, TimeUnit.SECONDS);
        verify(lock, times(1)).unlock();
    }

    @Test
    public void testPayOrderFetchFailed() throws Exception {
        when(orderService.getToPayOrder(anyString()))
            .thenThrow(new RuntimeException("get to pay order failed"));

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.pay("invalidOrderId");
        });

        Assertions.assertTrue(exception.getMessage().contains("get to pay order failed"));
    }

    @Test
    public void testPayOrderNotFound() throws Exception {
        when(orderService.getToPayOrder(anyString()))
            .thenReturn(null);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.pay("invalidOrderId");
        });

        Assertions.assertTrue(exception.getMessage().contains("not able to pay"));
    }

    @Test
    public void testPayWalletNotFound() throws Exception {
        SpOrder order = new SpOrder();
        order.setUserId("user123");
        when(orderService.getToPayOrder(anyString()))
            .thenReturn(order);
        when(walletDao.findByUserId(order.getUserId())).thenReturn(null);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.pay("validOrderId");
        });

        Assertions.assertTrue(exception.getMessage().contains("wallet not exists"));
    }

    @Test
    public void testPayInsufficientBalance() throws Exception {
        SpOrder order = prepareValidOrder();
        Wallet wallet = prepareWallet(BigDecimal.valueOf(50.00));
        Cart cart = prepareCart();
        List<Product> products = cart.getProductSet().stream().toList();

        when(orderService.getToPayOrder(anyString())).thenReturn(order);
        when(walletDao.findByUserId(anyString())).thenReturn(wallet);
        when(gson.fromJson(order.getCartJson(), new TypeToken<Cart>(){}.getType())).thenReturn(cart);
        when(productService.getProducts(anySet())).thenReturn(products);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            paymentService.pay("validOrderId");
        });
        Assertions.assertTrue(exception.getMessage().contains("balance is not enough"));
    }

    @Test
    public void testPaySuccess() throws Exception {
        SpOrder order = prepareValidOrder();
        Wallet wallet = prepareWallet(BigDecimal.valueOf(200.00));
        Cart cart = prepareCart();
        List<Product> products = cart.getProductSet().stream().toList();

        when(orderService.getToPayOrder(anyString())).thenReturn(order);
        when(walletDao.findByUserId(anyString())).thenReturn(wallet);
        when(gson.fromJson(order.getCartJson(), new TypeToken<Cart>(){}.getType())).thenReturn(cart);
        when(productService.getProducts(anySet())).thenReturn(products);
        when(walletDao.saveAndFlush(any(Wallet.class))).thenReturn(null);
        when(paymentLogDao.saveAndFlush(any(PaymentLog.class))).thenReturn(null);
        doNothing().when(localMessageService).saveOrderMessage(any(OrderMsg.class));
        doNothing().when(mqMessageService).send(any(OrderMsg.class));

        PaymentLog result = paymentService.pay("validOrderId");

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(order.getUserId(), result.getUserId());
        verify(walletDao, times(1)).saveAndFlush(any(Wallet.class));
        verify(paymentLogDao, times(1)).saveAndFlush(any(PaymentLog.class));
        verify(localMessageService, times(1)).saveOrderMessage(any(OrderMsg.class));
        verify(mqMessageService, times(1)).send(any(OrderMsg.class));
    }

    private SpOrder prepareValidOrder() {
        SpOrder order = mock(SpOrder.class);
        when(order.getUserId()).thenReturn("user123");
        when(order.getCartJson()).thenReturn("cartJsonData");
        return order;
    }

    private Wallet prepareWallet(BigDecimal balance) {
        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(balance);
        return wallet;
    }

    private Cart prepareCart() {
        Cart cart = mock(Cart.class);
        Product product1 = new Product("product1", BigDecimal.valueOf(50.00), 10, new Date());
        Product product2 = new Product("product2", BigDecimal.valueOf(30.00), 5, new Date());

        Map<String, Integer> counterMap = new HashMap<>();
        counterMap.put(product1.getId(), 2);
        counterMap.put(product2.getId(), 1);
        when(cart.getCounterMap()).thenReturn(counterMap);

        Set<Product> productSet = Set.of(product1, product2);
        when(cart.getProductSet()).thenReturn(productSet);
        return cart;
    }
}