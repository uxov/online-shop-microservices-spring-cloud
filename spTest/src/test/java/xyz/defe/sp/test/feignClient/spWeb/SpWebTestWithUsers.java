package xyz.defe.sp.test.feignClient.spWeb;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.test.Users;
import xyz.defe.sp.test.feignClient.UserService;
import xyz.defe.sp.test.restTemplate.services.spPayment.PaymentRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpWebTestWithUsers {
    @Autowired
    private SpWeb spWeb;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentRequest paymentRequest;
    private final int atLeast = 2;
    private final int count = 10000;
    private volatile boolean flag = true;
    private AtomicInteger taskCount = new AtomicInteger();
    private AtomicInteger getProductsSucCount = new AtomicInteger();
    private AtomicInteger loginSucCount = new AtomicInteger();
    private AtomicInteger getOrderTokenSucCount = new AtomicInteger();
    private AtomicInteger newOrderSucCount = new AtomicInteger();
    private AtomicInteger paySucCount = new AtomicInteger();
    private AtomicInteger getPaidOrderSucCount = new AtomicInteger();
    private Map<String, String> tokenMap = new HashMap<>();

    @RepeatedTest(3)
    public void run() throws InterruptedException {
        List<Account> accounts = getAccounts(count);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        System.out.println("start test");

        long start = System.currentTimeMillis();

        for (Account account : accounts) {
            CompletableFuture.runAsync(() -> request(account), executor)
                    .whenComplete((result, ex) -> {
                        if (taskCount.incrementAndGet() == count) {
                            flag = false;
                            System.out.println();
                            System.out.println("All tasks are completed !!!");
                            summary(start);
                            executor.shutdown();
                        }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
            Thread.sleep(15);   //control request rate
        }

        while (flag) {
            Thread.sleep(5000);
        }
    }

    @BeforeEach
    void beforeEach() throws IOException {
        readFlag();
    }

    private void readFlag() throws IOException {
        Path filePath = Path.of("src/test/resources/stopFlag");
        String flag = Files.readString(filePath).trim();
        int fg = Integer.parseInt(flag);
        System.out.println("flag = " + fg);
        if (fg == 0) {
            System.out.println("stop test");
            System.exit(0);
        }
    }

    private void summary(long start) {
        long end = System.currentTimeMillis();
        float secs = (end - start) / 1000f;
        System.out.println();
        System.out.println(taskCount.get() + " users requested");
        System.out.println(count * 6 + " requests");
        System.out.println("took " + secs + " seconds");
        System.out.println(newOrderSucCount.get() / secs + " orders per second");
        System.out.println((count * 6) / secs + " requests per second");
        System.out.println();
        System.out.println("successful requests count:");
        System.out.println("getProducts : " + getProductsSucCount.get());
        System.out.println("login : " + loginSucCount.get());
        System.out.println("getOrderToken : " + getOrderTokenSucCount.get());
        System.out.println("newOrder : " + newOrderSucCount.get());
        System.out.println("pay : " + paySucCount.get());
        System.out.println("getPaidOrder : " + getPaidOrderSucCount.get());
        System.out.println();
    }

    public void request(Account user) {
        Random random = new Random();

        // 1. get products
        List<Product> products = spWeb.getProducts(1, 3).getData();
        if (products == null || products.size() < 3) {
            if (products == null) {
                System.out.println("User : " + user.getName() + " getProducts failed - products is null");
            } else {
                System.out.println("User : " + user.getName() + " getProducts failed - products.size() < 3");
            }
            System.out.println("getProducts  return");
            return;
        }
        for (Product p : products) {
            if (p.getQuantity() < atLeast) {
                System.out.println("User : " + user.getName()
                        + " getProducts failed - product is out of stock,product id:" + p.getId());
                return;
            }
        }
        getProductsSucCount.incrementAndGet();

        // 2. user login
        String token = "";
        if (tokenMap.get(user.getId()) == null) {
            Map<String, String> map = spWeb.login(user.getUname(), user.getPwd()).getData();
            token = map.get("token");
            loginSucCount.incrementAndGet();
            tokenMap.put(user.getId(), token);
        } else {
            token = tokenMap.get(user.getId());
        }


        // 3. add products to cart and submit the order
        String orderToken = spWeb.getOrderToken(token).getData();
        if (Strings.isNullOrEmpty(orderToken)) {
            System.out.println("User : " + user.getName() + " getOrderToken failed - orderToken isNullOrEmpty");
            return;
        }
        getOrderTokenSucCount.incrementAndGet();
        Cart cart = new Cart();
        cart.setUid(user.getId());
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), random.nextInt(2) + 1);
        cart.getCounterMap().put(products.get(1).getId(), random.nextInt(2) + 1);
        cart.getCounterMap().put(products.get(2).getId(), random.nextInt(2) + 1);
        SpOrder order = spWeb.newOrder(cart, token).getData();
        if (order == null || !order.isValid()) {
            if (order == null) {
                System.out.println("User : " + user.getName() + " newOrder failed - order is null");
            } else {
                System.out.println("User : " + user.getName() + " newOrder failed - order is invalid");
            }
            return;
        }
        newOrderSucCount.incrementAndGet();

        // 4. pay the order
        PaymentLog record = spWeb.pay(order.getId(), token).getData();
        if (record == null || !order.getId().equals(record.getOrderId())) {
            if (record == null ) {
                System.out.println("User : " + user.getName() + " pay failed - record is null");
            } else {
                System.out.println("User : " + user.getName() + " pay failed - !order.getId().equals(record.getOrderId())");
            }
            return;
        }
        paySucCount.incrementAndGet();

        // 5. get the paid order
        order = spWeb.getPaidOrder(order.getId(), token).getData();
        if (order == null || 2 != order.getPaymentState()) {
            if (order == null) {
                System.out.println("User : " + user.getName() + " getPaidOrder failed - order is null");
            } else {
                System.out.println("User : " + user.getName() + " getPaidOrder failed - (2 !== order.getPaymentState())");
            }
            return;
        }
        getPaidOrderSucCount.incrementAndGet();
    }

//    @Test
    public void testGetAccounts() {
        List<Account> accounts = getAccounts(10);
        assertEquals(10, accounts.size());
        assertTrue(accounts.get(0).getUname().equals(Users.ALEN.uname));
    }

    public List<Account> addTestUsers(List<Account> existAccList, int n) {
        Set<String> existsSet = existAccList.stream().map(a -> a.getId()).collect(Collectors.toSet());
        Set<String> newSet = new HashSet<>(existsSet);
        List<Account> newAccounts = new ArrayList<>();
        while (newSet.size() < n) {
            String uname = UUID.randomUUID().toString().substring(0, 8);
            if (!newSet.contains(uname)) {
                Account account = new Account();
                account.setUname(uname);
                account.setPwd("123");
                account.setName(uname.toUpperCase());
                account.setAge(22);
                account.setSex("x");
                createUserWallet(account);
                newSet.add(uname);
                newAccounts.add(account);
            }
        }
        ResponseData responseData = userService.createAccounts(newAccounts);
        assertEquals(200, responseData.getStatus());
        newAccounts.addAll(existAccList);
        return newAccounts;
    }

    public List<Account> getAccounts(int n) {
        List<Account> accountList;
        List<Account> existAccList = userService.getAll().getData();
        if (existAccList.size() < n) {
            accountList = addTestUsers(existAccList, n);
            System.out.println("addTestUsers()");
        } else {
            accountList = existAccList;
        }
        if (accountList.size() > n) {
            return accountList.subList(0, n);
        }
        return accountList;
    }

    void createUserWallet(Account user) {
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setBalance(new BigDecimal(100000000.00));
        wallet.setCreatedTime(new Date());
        Wallet wa = paymentRequest.createUserWallet(wallet).getData();
        assertNotNull(wallet.getBalance());
        System.out.println("created " + user.getName() + "'s wallet");
    }
}


