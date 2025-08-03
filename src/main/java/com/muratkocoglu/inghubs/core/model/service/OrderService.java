package com.muratkocoglu.inghubs.core.model.service;

import java.time.LocalDate;
import java.util.List;

import com.muratkocoglu.inghubs.core.dto.OrderRequestDTO;
import com.muratkocoglu.inghubs.core.dto.OrderResponseDTO;


public interface OrderService {

	OrderResponseDTO createOrder(OrderRequestDTO dto);
	
	void cancelOrder(Long orderId, Long customerId, String role);
	
	List<OrderResponseDTO> listOrders(Long customerId, LocalDate startDate, LocalDate endDate);
	
	void matchOrder(Long orderId);
}
