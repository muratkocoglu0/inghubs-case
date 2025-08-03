package com.muratkocoglu.inghubs.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.muratkocoglu.inghubs.core.model.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
