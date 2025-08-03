package com.muratkocoglu.inghubs.core.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.muratkocoglu.inghubs.core.model.entity.Asset;
import com.muratkocoglu.inghubs.core.model.repository.AssetRepository;
import com.muratkocoglu.inghubs.core.model.service.AssetService;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AssetRepositoryConcurrencyTest {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetService assetService;
    
    private static final String ASSET_NAME = "TRY";

    @Test
    void concurrentReserveBuyBalanceTest() throws InterruptedException {
        int threadCount = 5;
        BigDecimal requestAmount = new BigDecimal("30000");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    assetService.reserveBuyBalance(1L, requestAmount);
                } catch (Throwable e) {
                    exceptions.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        Asset finalAsset = assetRepository
                .findByCustomerIdAndAssetName(1L, ASSET_NAME)
                .orElseThrow();

        assertTrue(finalAsset.getUsableSize().compareTo(BigDecimal.ZERO) >= 0,
                "Usable size should not be negative");

        assertTrue(exceptions.stream()
                        .anyMatch(e -> e.getMessage().contains("Insufficient TRY balance")),
                "Some threads should get insufficient balance error");
    }
}
