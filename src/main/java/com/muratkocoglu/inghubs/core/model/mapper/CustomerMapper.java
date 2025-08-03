package com.muratkocoglu.inghubs.core.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.muratkocoglu.inghubs.core.dto.CustomerRequestDTO;
import com.muratkocoglu.inghubs.core.dto.CustomerResponseDTO;
import com.muratkocoglu.inghubs.core.model.entity.Customer;

@Mapper
public interface CustomerMapper {

	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
	
	Customer toEntity(CustomerRequestDTO dto);
	
	CustomerResponseDTO toDTO(Customer customer);
	
	default Customer toCustomer(Long customerId) {
        Customer customer = new Customer();
        customer.setId(customerId);
        return customer;
    }
}
