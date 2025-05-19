package xyz.defe.sp.product.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductQuantityServiceTest {

    @Mock
    private Gson gson;
    @Mock
    private ProductDao productDao;
    @Mock
    private DeductQuantityLogDao deductQuantityLogDao;

    @InjectMocks
    private DeductQuantityService deductQuantityService;

    @Test
    void successfullyDeductQuantitiesWhenCounterMapIsEmpty() {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            deductQuantityService.deductQuantity(orderId, counterMap);
        });
        assertTrue(ex.getMessage().contains("counterMap is null or empty"));
    }

    @Test
    void successfullyDeductQuantitiesWhenAllProductsHaveSufficientStock() {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);
        Product product1 = new Product();
        product1.setId("product1");
        product1.setQuantity(5);
        Product product2 = new Product();
        product2.setId("product2");
        product2.setQuantity(3);

        when(productDao.findAllById(counterMap.keySet())).thenReturn(List.of(product1, product2));
        when(deductQuantityLogDao.saveAndFlush(any(DeductQuantityLog.class))).thenReturn(new DeductQuantityLog());

        deductQuantityService.deductQuantity(orderId, counterMap);

        assertEquals(3, product1.getQuantity());
        assertEquals(2, product2.getQuantity());
        verify(productDao).saveAll(anyList());
        ArgumentCaptor<DeductQuantityLog> logCaptor = ArgumentCaptor.forClass(DeductQuantityLog.class);
        verify(deductQuantityLogDao).saveAndFlush(logCaptor.capture());
        assertEquals(orderId, logCaptor.getValue().getOrderId());
    }

    @Test
    void throwExceptionWhenAnyProductIsOutOfStock() {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 6, "product2", 1);
        Product product1 = new Product();
        product1.setId("product1");
        product1.setQuantity(5);
        Product product2 = new Product();
        product2.setId("product2");
        product2.setQuantity(3);

        when(productDao.findAllById(counterMap.keySet())).thenReturn(List.of(product1, product2));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            deductQuantityService.deductQuantity(orderId, counterMap);
        });
        assertTrue(ex.getMessage().contains("product is out of stock"));
        verify(productDao, never()).saveAll(anyList());
        verify(deductQuantityLogDao, never()).saveAndFlush(any(DeductQuantityLog.class));
    }
}