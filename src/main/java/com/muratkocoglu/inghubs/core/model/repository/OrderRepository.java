package com.muratkocoglu.inghubs.core.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.muratkocoglu.inghubs.core.model.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
	List<Order> findByCustomerId(Long customerId);
    
	List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);
}
