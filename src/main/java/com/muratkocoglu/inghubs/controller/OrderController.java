package com.muratkocoglu.inghubs.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.muratkocoglu.inghubs.core.configuration.CheckCustomerAccess;
import com.muratkocoglu.inghubs.core.dto.CustomUserDetails;
import com.muratkocoglu.inghubs.core.dto.OrderRequestDTO;
import com.muratkocoglu.inghubs.core.dto.OrderResponseDTO;
import com.muratkocoglu.inghubs.core.dto.RestResponse;
import com.muratkocoglu.inghubs.core.model.service.impl.OrderServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @CheckCustomerAccess("customerId")
    public RestResponse<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        return RestResponse.of(orderService.createOrder(dto), null, null);
    }
    
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public RestResponse<Void> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        orderService.cancelOrder(orderId, user.getCustomerId(), user.getRole());
        return RestResponse.of(null, null, "Order cancelled successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @CheckCustomerAccess("customerId")
    public RestResponse<List<OrderResponseDTO>> listOrders(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate) {
        return RestResponse.of(orderService.listOrders(customerId, startDate, endDate), null, null);
    }

    @PostMapping("/{orderId}/match")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<Void> matchOrder(@PathVariable Long orderId) {
        orderService.matchOrder(orderId);
        return RestResponse.of(null, null, "Order matched successfully");
    }
}
