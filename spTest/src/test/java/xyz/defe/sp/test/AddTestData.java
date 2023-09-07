package xyz.defe.sp.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.test.restTemplate.services.spPayment.PaymentRequest;
import xyz.defe.sp.test.restTemplate.services.spProduct.ProductRequest;
import xyz.defe.sp.test.restTemplate.services.spUser.SpUserRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        List<Product> products = productRequest.getProducts().getData();
        if (products.isEmpty()) {
            addProducts();
        }

        for (Users user : Users.values()) {
            Account account = spUserRequest.verify(user.uname, user.pwd).getData();
            if (account == null) {
                createAccount(user);
            }
        }
    }

    void addProducts(){
        List products = List.of(
                new Product("Computer", new BigDecimal(3500.00), 100000, new Date()),
                new Product("Cell Phone", new BigDecimal(1100.00), 100000, new Date()),
                new Product("Bike", new BigDecimal(500.00), 100000, new Date())
        );
        ResponseData responseData = productRequest.addProducts(products);
        assertEquals(200, responseData.getStatus());
    }

    void createAccount(Users user) {
        Account a = new Account();
        a.setName(user.name);
        a.setUname(user.uname);
        a.setSex(user.sex);
        a.setAge(user.age);
        a.setPwd(user.pwd);
        ResponseData responseData = spUserRequest.createAccount(List.of(a));
        assertEquals(200, responseData.getStatus());
        createUserWallet(a);
    }

    void createUserWallet(Account user) {
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setBalance(new BigDecimal(1000000.00));
        wallet.setCreatedTime(new Date());
        Wallet wa = paymentRequest.createUserWallet(wallet).getData();
        assertNotNull(wallet.getBalance());
        System.out.println("created " + user.getName() + "'s wallet");
    }

}
