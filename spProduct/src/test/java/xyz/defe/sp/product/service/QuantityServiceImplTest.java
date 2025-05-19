package xyz.defe.sp.product.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.RedissonMultiLock;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuantityServiceImplTest {
    @Mock
    private ProductDao productDao;
    @Mock
    private ProductLockService productLockService;
    @Mock
    private DeductQuantityLogDao deductQuantityLogDao;
    @Mock
    private DeductQuantityService deductQuantityService;
    @Mock
    private RestoreQuantityService restoreQuantityService;

    @InjectMocks
    private QuantityServiceImpl quantityService;

    @Test
    @DisplayName("Successfully deduct quantities when order is valid and not processed")
    void successfullyDeductQuantitiesWhenOrderIsValidAndNotProcessed() throws InterruptedException {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);

        RedissonMultiLock lock = mock(RedissonMultiLock.class);
        when(productLockService.getProductMultiLock(counterMap.keySet())).thenReturn(lock);
        when(lock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(true);
        when(deductQuantityLogDao.findByOrderId(orderId)).thenReturn(null);

        doNothing().when(deductQuantityService).deductQuantity(orderId, counterMap);

        quantityService.checkAndDeduct(orderId, counterMap);

        verify(deductQuantityService).deductQuantity(orderId, counterMap);
        verify(lock).unlock();
    }

    @Test
    @DisplayName("Skip deduction when order is already processed")
    void skipDeductionWhenOrderIsAlreadyProcessed() throws InterruptedException {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);

        RedissonMultiLock lock = mock(RedissonMultiLock.class);
        when(productLockService.getProductMultiLock(counterMap.keySet())).thenReturn(lock);
        when(lock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(true);
        when(deductQuantityLogDao.findByOrderId(orderId)).thenReturn(new DeductQuantityLog());

        quantityService.checkAndDeduct(orderId, counterMap);

        verify(deductQuantityService, never()).deductQuantity(anyString(), anyMap());
        verify(lock).unlock();
    }

    @Test
    @DisplayName("Throw exception when lock acquisition fails during deduction")
    void throwExceptionWhenLockAcquisitionFailsDuringDeduction() throws InterruptedException {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);

        RedissonMultiLock lock = mock(RedissonMultiLock.class);
        when(productLockService.getProductMultiLock(counterMap.keySet())).thenReturn(lock);
        when(lock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(false);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            quantityService.checkAndDeduct(orderId, counterMap);
        });

        Assertions.assertEquals("tryLock() failed in checkAndDeduct()", exception.getMessage());
        verify(deductQuantityLogDao, never()).findByOrderId(anyString());
        verify(deductQuantityService, never()).deductQuantity(anyString(), anyMap());
        verify(lock).unlock();
    }

    @Test
    @DisplayName("Successfully restore quantities when order is valid and processed")
    void successfullyRestoreQuantitiesWhenOrderIsValidAndProcessed() throws InterruptedException {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);

        RedissonMultiLock lock = mock(RedissonMultiLock.class);
        when(productLockService.getProductMultiLock(counterMap.keySet())).thenReturn(lock);
        when(lock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(true);
        DeductQuantityLog record = new DeductQuantityLog();
        when(deductQuantityLogDao.findByOrderIdAndState(orderId, 1)).thenReturn(record);

        doNothing().when(restoreQuantityService).restoreQuantity(orderId, counterMap, record);

        quantityService.checkAndRestore(orderId, counterMap);

        verify(restoreQuantityService).restoreQuantity(orderId, counterMap, record);
        verify(lock).unlock();
    }

    @Test
    @DisplayName("Skip restoration when order is not processed")
    void skipRestorationWhenOrderIsNotProcessed() throws InterruptedException {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);

        RedissonMultiLock lock = mock(RedissonMultiLock.class);
        when(productLockService.getProductMultiLock(counterMap.keySet())).thenReturn(lock);
        when(lock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(true);
        when(deductQuantityLogDao.findByOrderIdAndState(orderId, 1)).thenReturn(null);

        quantityService.checkAndRestore(orderId, counterMap);

        verify(restoreQuantityService, never()).restoreQuantity(anyString(), anyMap(), any());
        verify(lock).unlock();
    }

    @Test
    @DisplayName("Throw exception when lock acquisition fails during restoration")
    void throwExceptionWhenLockAcquisitionFailsDuringRestoration() throws InterruptedException {
        String orderId = "order123";
        Map<String, Integer> counterMap = Map.of("product1", 2, "product2", 1);

        RedissonMultiLock lock = mock(RedissonMultiLock.class);
        when(productLockService.getProductMultiLock(counterMap.keySet())).thenReturn(lock);
        when(lock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(false);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            quantityService.checkAndRestore(orderId, counterMap);
        });

        Assertions.assertEquals("tryLock() failed in checkAndDeduct()", exception.getMessage());
        verify(deductQuantityLogDao, never()).findByOrderIdAndState(anyString(), anyInt());
        verify(restoreQuantityService, never()).restoreQuantity(anyString(), anyMap(), any());
        verify(lock).unlock();
    }
}