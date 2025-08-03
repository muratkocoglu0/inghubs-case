package com.muratkocoglu.inghubs.core.model.service;

import com.muratkocoglu.inghubs.core.dto.CustomerRequestDTO;
import com.muratkocoglu.inghubs.core.dto.CustomerResponseDTO;

public interface CustomerService {

	CustomerResponseDTO getCustomerById(Long id);
	
	CustomerResponseDTO createCustomer(CustomerRequestDTO dto);
}
