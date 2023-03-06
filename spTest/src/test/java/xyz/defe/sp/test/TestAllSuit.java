package xyz.defe.sp.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import xyz.defe.sp.test.feignClient.OrderServiceTest;
import xyz.defe.sp.test.feignClient.PaymentServiceTest;
import xyz.defe.sp.test.feignClient.ProductServiceTest;
import xyz.defe.sp.test.feignClient.UserServiceTest;
import xyz.defe.sp.test.feignClient.spWeb.SpWebTest;
import xyz.defe.sp.test.restTemplate.SpWebRestTest;
import xyz.defe.sp.test.restTemplate.services.spOrder.OrderTest;
import xyz.defe.sp.test.restTemplate.services.spPayment.PaymentTest;
import xyz.defe.sp.test.restTemplate.services.spProduct.ProductTest;
import xyz.defe.sp.test.restTemplate.services.spUser.SpUserTest;

@Suite
@SelectClasses({
        AddTestData.class,
        SpUserTest.class, ProductTest.class, OrderTest.class, PaymentTest.class,    //rest
        UserServiceTest.class, ProductServiceTest.class, OrderServiceTest.class, PaymentServiceTest.class,   //feign
        SpWebRestTest.class,   //rest
        SpWebTest.class     //feign
})
public class TestAllSuit {
}
