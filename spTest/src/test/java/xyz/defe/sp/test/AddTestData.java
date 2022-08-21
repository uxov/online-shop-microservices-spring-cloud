package xyz.defe.sp.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.test.restTemplate.services.spUser.SpUserRequest;
import xyz.defe.sp.test.restTemplate.services.spPayment.PaymentRequest;
import xyz.defe.sp.test.restTemplate.services.spProduct.ProductRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class AddTestData {
    @Autowired
    private ProductRequest productRequest;
    @Autowired
    private SpUserRequest spUserRequest;
    @Autowired
    private PaymentRequest paymentRequest;

    /**
     * add test data to database
     */
    @Test
    void addData(){
        addProducts();
        List<Account> users = createAccounts();
        createUserWallet(users);
    }

    void addProducts(){
        List products = new ArrayList();

        Product p0 = new Product();
        p0.setName("Computer");
        p0.setPrice(new BigDecimal(3500.00));
        p0.setQuantity(100);
        p0.setCreatedTime(new Date());
        products.add(p0);

        Product p1 = new Product();
        p1.setName("Cell Phone");
        p1.setPrice(new BigDecimal(1100.00));
        p1.setQuantity(100);
        p1.setCreatedTime(new Date());
        products.add(p1);

        Product p2 = new Product();
        p2.setName("Bike");
        p2.setPrice(new BigDecimal(500.00));
        p2.setQuantity(100);
        p2.setCreatedTime(new Date());
        products.add(p2);

        ResponseData responseData = productRequest.addProducts(products);
        Assertions.assertEquals(200, responseData.getStatus());
    }

    List<Account> createAccounts() {
        List<Account> accounts = new ArrayList<>();

        Account u1 = new Account();
        u1.setName("Alen");
        u1.setSex("female");
        u1.setAge(21);
        u1.setUname("alen");
        u1.setPwd("123");
        accounts.add(u1);

        Account u2 = new Account();
        u2.setName("Mike");
        u2.setSex("male");
        u2.setAge(22);
        u2.setUname("mike");
        u2.setPwd("123");
        accounts.add(u2);

        Account u3 = new Account();
        u3.setName("Jhon");
        u3.setSex("male");
        u3.setAge(23);
        u3.setUname("jhon");
        u3.setPwd("123");
        accounts.add(u3);

        ResponseData responseData = spUserRequest.createAccount(accounts);
        Assertions.assertEquals(200, responseData.getStatus());

        return accounts;
    }

    void createUserWallet(List<Account> users) {
        users.forEach(user -> {
            Wallet wallet = new Wallet();
            wallet.setUserId(user.getId());
            wallet.setBalance(new BigDecimal(100000.00));
            wallet.setCreatedTime(new Date());
            wallet = paymentRequest.createUserWallet(wallet);
            Assertions.assertNotNull(wallet);
            Assertions.assertNotNull(wallet.getId());
            System.out.println("created " + user.getName() + "'s wallet");
        });
    }

}
