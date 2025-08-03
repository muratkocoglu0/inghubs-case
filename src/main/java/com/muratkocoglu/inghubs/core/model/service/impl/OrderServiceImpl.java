package com.muratkocoglu.inghubs.core.model.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muratkocoglu.inghubs.core.dto.OrderRequestDTO;
import com.muratkocoglu.inghubs.core.dto.OrderResponseDTO;
import com.muratkocoglu.inghubs.core.dto.OrderSide;
import com.muratkocoglu.inghubs.core.dto.OrderStatus;
import com.muratkocoglu.inghubs.core.model.entity.Order;
import com.muratkocoglu.inghubs.core.model.mapper.OrderMapper;
import com.muratkocoglu.inghubs.core.model.repository.OrderRepository;
import com.muratkocoglu.inghubs.core.model.service.AssetService;
import com.muratkocoglu.inghubs.core.model.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	
	private final OrderRepository orderRepository;
    private final AssetService assetService;
	
    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
    @Override
	public OrderResponseDTO createOrder(OrderRequestDTO dto) {
    	    	
    	if (dto.getSide() == OrderSide.BUY) {
            assetService.reserveBuyBalance(dto.getCustomerId(), dto.getPrice().multiply(dto.getSize()));
        } else {
            assetService.reserveSellBalance(dto.getCustomerId(), dto.getAssetName(), dto.getSize());
        }
    	
        Order savedOrder = orderRepository.save(OrderMapper.INSTANCE.toEntity(dto));
        return OrderMapper.INSTANCE.toDto(savedOrder);
	}
    
    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
	@Override
	public void cancelOrder(Long orderId, Long customerId, String role) {
    	Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

    	if ("CUSTOMER".equals(role) && !customerId.equals(order.getCustomer().getId())) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
    	
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be cancelled");
        }
        
        if (order.getSide() == OrderSide.BUY) {
            assetService.refundBuyBalance(order.getCustomer().getId(), order.getPrice().multiply(order.getSize()));
        } else {
            assetService.refundSellBalance(order.getCustomer().getId(), order.getAssetName(), order.getSize());
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
		
	}
    
    @Cacheable(value = "orders", key = "#customerId + '_' + #startDate + '_' + #endDate")
	@Override
	public List<OrderResponseDTO> listOrders(Long customerId, LocalDate startDate, LocalDate endDate) {	
    	LocalDateTime startDateTime = startDate.atStartOfDay();
    	LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		List<Order> orderList = orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDateTime, endDateTime);
		return OrderMapper.INSTANCE.toDTOList(orderList); 
	}
    
    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
	@Override
	public void matchOrder(Long orderId) {
    	Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be matched");
        }

        if (order.getSide() == OrderSide.BUY) {
            assetService.creditAsset(order.getCustomer().getId(), order.getAssetName(), order.getSize());
        } 
        else {
            assetService.creditAsset(order.getCustomer().getId(), "TRY", order.getPrice().multiply(order.getSize()));
        }

        order.setStatus(OrderStatus.MATCHED);
        orderRepository.save(order);
		
	}
    
}
