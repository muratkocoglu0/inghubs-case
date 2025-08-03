package com.muratkocoglu.inghubs.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import com.muratkocoglu.inghubs.core.dto.OrderRequestDTO;
import com.muratkocoglu.inghubs.core.dto.OrderResponseDTO;
import com.muratkocoglu.inghubs.core.dto.OrderSide;
import com.muratkocoglu.inghubs.core.dto.OrderStatus;
import com.muratkocoglu.inghubs.core.model.entity.Customer;
import com.muratkocoglu.inghubs.core.model.entity.Order;
import com.muratkocoglu.inghubs.core.model.repository.OrderRepository;
import com.muratkocoglu.inghubs.core.model.service.AssetService;
import com.muratkocoglu.inghubs.core.model.service.impl.OrderServiceImpl;

class OrderServiceImplTest {

	@Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Order pendingBuyOrder;
    private Order pendingSellOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);

        pendingBuyOrder = new Order();
        pendingBuyOrder.setId(100L);
        pendingBuyOrder.setCustomer(customer);
        pendingBuyOrder.setStatus(OrderStatus.PENDING);
        pendingBuyOrder.setSide(OrderSide.BUY);
        pendingBuyOrder.setPrice(BigDecimal.valueOf(10));
        pendingBuyOrder.setSize(BigDecimal.valueOf(2));
        pendingBuyOrder.setAssetName("AAPL");

        pendingSellOrder = new Order();
        pendingSellOrder.setId(101L);
        pendingSellOrder.setCustomer(customer);
        pendingSellOrder.setStatus(OrderStatus.PENDING);
        pendingSellOrder.setSide(OrderSide.SELL);
        pendingSellOrder.setPrice(BigDecimal.valueOf(50));
        pendingSellOrder.setSize(BigDecimal.valueOf(5));
        pendingSellOrder.setAssetName("TSLA");
    }

    @Test
    void createOrder_shouldReserveBuyBalance_whenSideIsBuy() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setCustomerId(1L);
        dto.setSide(OrderSide.BUY);
        dto.setPrice(BigDecimal.TEN);
        dto.setSize(BigDecimal.valueOf(2));
        dto.setAssetName("AAPL");

        when(orderRepository.save(any(Order.class))).thenReturn(pendingBuyOrder);

        OrderResponseDTO response = orderService.createOrder(dto);

        verify(assetService).reserveBuyBalance(1L, BigDecimal.valueOf(20));
        verify(orderRepository).save(any(Order.class));
        assertNotNull(response);
    }

    @Test
    void createOrder_shouldReserveSellBalance_whenSideIsSell() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setCustomerId(1L);
        dto.setSide(OrderSide.SELL);
        dto.setPrice(BigDecimal.valueOf(50));
        dto.setSize(BigDecimal.valueOf(5));
        dto.setAssetName("TSLA");

        when(orderRepository.save(any(Order.class))).thenReturn(pendingSellOrder);

        OrderResponseDTO response = orderService.createOrder(dto);

        verify(assetService).reserveSellBalance(1L, "TSLA", BigDecimal.valueOf(5));
        verify(orderRepository).save(any(Order.class));
        assertNotNull(response);
    }

    @Test
    void cancelOrder_shouldCancelBuyOrderAndRefundBalance_whenCustomerOwnsOrder() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(pendingBuyOrder));

        orderService.cancelOrder(100L, 1L, "CUSTOMER");

        assertEquals(OrderStatus.CANCELED, pendingBuyOrder.getStatus());
        verify(assetService).refundBuyBalance(1L, BigDecimal.valueOf(20));
        verify(orderRepository).save(pendingBuyOrder);
    }

    @Test
    void cancelOrder_shouldCancelSellOrderAndRefundBalance_whenCustomerOwnsOrder() {
        when(orderRepository.findById(101L)).thenReturn(Optional.of(pendingSellOrder));

        orderService.cancelOrder(101L, 1L, "CUSTOMER");

        assertEquals(OrderStatus.CANCELED, pendingSellOrder.getStatus());
        verify(assetService).refundSellBalance(1L, "TSLA", BigDecimal.valueOf(5));
        verify(orderRepository).save(pendingSellOrder);
    }

    @Test
    void cancelOrder_shouldAllowAdminToCancelOtherCustomersOrder() {
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        pendingBuyOrder.setCustomer(otherCustomer);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(pendingBuyOrder));

        orderService.cancelOrder(100L, 1L, "ADMIN");

        assertEquals(OrderStatus.CANCELED, pendingBuyOrder.getStatus());
        verify(assetService).refundBuyBalance(2L, BigDecimal.valueOf(20));
        verify(orderRepository).save(pendingBuyOrder);
    }

    @Test
    void cancelOrder_shouldThrowAccessDenied_whenCustomerTriesToCancelOthersOrder() {
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        pendingBuyOrder.setCustomer(otherCustomer);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(pendingBuyOrder));

        assertThrows(AccessDeniedException.class,
                () -> orderService.cancelOrder(100L, 1L, "CUSTOMER"));

        verify(assetService, never()).refundBuyBalance(any(), any());
        verify(assetService, never()).refundSellBalance(any(), any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_shouldThrowRuntimeException_whenOrderIsNotPending() {
        pendingBuyOrder.setStatus(OrderStatus.MATCHED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(pendingBuyOrder));

        assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(100L, 1L, "CUSTOMER"));

        verify(assetService, never()).refundBuyBalance(any(), any());
        verify(assetService, never()).refundSellBalance(any(), any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_shouldThrowRuntimeException_whenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(999L, 1L, "CUSTOMER"));

        verify(assetService, never()).refundBuyBalance(any(), any());
        verify(assetService, never()).refundSellBalance(any(), any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void listOrders_shouldReturnOrdersForCustomerAndDateRange() {
    	LocalDate start = LocalDate.now().minusDays(1);
    	LocalDate end = LocalDate.now().plusDays(1);
    	
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        when(orderRepository.findByCustomerIdAndCreateDateBetween(1L, startTime, endTime))
                .thenReturn(List.of(pendingBuyOrder, pendingSellOrder));

        List<OrderResponseDTO> result = orderService.listOrders(1L, start, end);

        assertEquals(2, result.size());
        verify(orderRepository).findByCustomerIdAndCreateDateBetween(1L, startTime, endTime);
    }

    @Test
    void matchOrder_shouldCreditAssetAndSetStatusMatched_whenSideIsBuy() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(pendingBuyOrder));

        orderService.matchOrder(100L);

        assertEquals(OrderStatus.MATCHED, pendingBuyOrder.getStatus());
        verify(assetService).creditAsset(1L, "AAPL", BigDecimal.valueOf(2));
        verify(orderRepository).save(pendingBuyOrder);
    }

    @Test
    void matchOrder_shouldCreditTryAndSetStatusMatched_whenSideIsSell() {
        when(orderRepository.findById(101L)).thenReturn(Optional.of(pendingSellOrder));

        orderService.matchOrder(101L);

        assertEquals(OrderStatus.MATCHED, pendingSellOrder.getStatus());
        verify(assetService).creditAsset(1L, "TRY", BigDecimal.valueOf(250));
        verify(orderRepository).save(pendingSellOrder);
    }

    @Test
    void matchOrder_shouldThrowRuntimeException_whenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> orderService.matchOrder(999L));

        verify(assetService, never()).creditAsset(any(), any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void matchOrder_shouldThrowRuntimeException_whenOrderNotPending() {
        pendingBuyOrder.setStatus(OrderStatus.CANCELED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(pendingBuyOrder));

        assertThrows(RuntimeException.class,
                () -> orderService.matchOrder(100L));

        verify(assetService, never()).creditAsset(any(), any(), any());
        verify(orderRepository, never()).save(any());
    }
}
