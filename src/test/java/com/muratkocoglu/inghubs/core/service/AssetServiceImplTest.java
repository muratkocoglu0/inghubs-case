package com.muratkocoglu.inghubs.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.muratkocoglu.inghubs.core.dto.AssetResponseDTO;
import com.muratkocoglu.inghubs.core.model.entity.Asset;
import com.muratkocoglu.inghubs.core.model.entity.Customer;
import com.muratkocoglu.inghubs.core.model.repository.AssetRepository;
import com.muratkocoglu.inghubs.core.model.service.impl.AssetServiceImpl;

class AssetServiceImplTest {

	@Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetServiceImpl assetService;
    
    @Autowired
    private PlatformTransactionManager transactionManager;

    private Customer customer;
    private Asset tryAsset;
    private Asset stockAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);

        tryAsset = new Asset();
        tryAsset.setCustomer(customer);
        tryAsset.setName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(1000));

        stockAsset = new Asset();
        stockAsset.setCustomer(customer);
        stockAsset.setName("AAPL");
        stockAsset.setSize(BigDecimal.valueOf(50));
        stockAsset.setUsableSize(BigDecimal.valueOf(50));
    }
    
    @Test
    void shouldBlockSecondTransactionUntilFirstCommits() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Void> t1 = executor.submit(() -> {
            new TransactionTemplate(transactionManager).execute(status -> {
                Asset asset = assetRepository
                        .findByCustomerIdAndAssetName(1L, "TRY")
                        .orElseThrow();
                asset.setSize(asset.getSize().add(BigDecimal.ONE));

                latch.countDown();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                return null;
            });
            return null;
        });

        Future<Long> t2 = executor.submit(() -> {
            latch.await();
            long start = System.currentTimeMillis();

            new TransactionTemplate(transactionManager).execute(status -> {
                assetRepository
                        .findByCustomerIdAndAssetName(1L, "TRY")
                        .orElseThrow();
                System.out.println("Thread 2 got the lock after waiting");
                return null;
            });

            return System.currentTimeMillis() - start;
        });

        Long waitTime = t2.get();
        t1.get();

        assertTrue(waitTime >= 2500, "Second transaction should have waited due to pessimistic lock");
    }

    @Test
    void getAssets_shouldReturnAssetList() {
        when(assetRepository.findByCustomerId(1L)).thenReturn(List.of(tryAsset, stockAsset));

        List<AssetResponseDTO> result = assetService.getAssets(1L);

        assertEquals(2, result.size());
        verify(assetRepository).findByCustomerId(1L);
    }

    @Test
    void reserveBuyBalance_shouldSubtractAmount_whenEnoughBalance() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        assetService.reserveBuyBalance(1L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(800), tryAsset.getUsableSize());
        verify(assetRepository).save(tryAsset);
    }

    @Test
    void reserveBuyBalance_shouldThrowException_whenInsufficientBalance() {
        tryAsset.setUsableSize(BigDecimal.valueOf(100));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> assetService.reserveBuyBalance(1L, BigDecimal.valueOf(500)));

        assertTrue(ex.getMessage().contains("Insufficient TRY balance"));
        verify(assetRepository, never()).save(any());
    }

    @Test
    void reserveSellBalance_shouldSubtractQuantity_whenEnoughStock() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(stockAsset));

        assetService.reserveSellBalance(1L, "AAPL", BigDecimal.valueOf(20));

        assertEquals(BigDecimal.valueOf(30), stockAsset.getUsableSize());
        verify(assetRepository).save(stockAsset);
    }

    @Test
    void reserveSellBalance_shouldThrowException_whenInsufficientStock() {
        stockAsset.setUsableSize(BigDecimal.valueOf(5));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(stockAsset));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> assetService.reserveSellBalance(1L, "AAPL", BigDecimal.valueOf(10)));

        assertTrue(ex.getMessage().contains("Insufficient AAPL balance"));
        verify(assetRepository, never()).save(any());
    }

    @Test
    void refundBuyBalance_shouldIncreaseTryUsableSize() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        assetService.refundBuyBalance(1L, BigDecimal.valueOf(300));

        assertEquals(BigDecimal.valueOf(1300), tryAsset.getUsableSize());
        verify(assetRepository).save(tryAsset);
    }

    @Test
    void refundSellBalance_shouldIncreaseStockUsableSize() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(stockAsset));

        assetService.refundSellBalance(1L, "AAPL", BigDecimal.valueOf(15));

        assertEquals(BigDecimal.valueOf(65), stockAsset.getUsableSize());
        verify(assetRepository).save(stockAsset);
    }

    @Test
    void creditAsset_shouldIncreaseSizeAndUsableSize() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(stockAsset));

        assetService.creditAsset(1L, "AAPL", BigDecimal.valueOf(10));

        assertEquals(BigDecimal.valueOf(60), stockAsset.getSize());
        assertEquals(BigDecimal.valueOf(60), stockAsset.getUsableSize());
        verify(assetRepository).save(stockAsset);
    }
}