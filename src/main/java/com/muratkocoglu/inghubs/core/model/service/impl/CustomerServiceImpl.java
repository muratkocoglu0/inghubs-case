package com.muratkocoglu.inghubs.core.model.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muratkocoglu.inghubs.core.dto.CustomerRequestDTO;
import com.muratkocoglu.inghubs.core.dto.CustomerResponseDTO;
import com.muratkocoglu.inghubs.core.dto.UserRole;
import com.muratkocoglu.inghubs.core.model.entity.Customer;
import com.muratkocoglu.inghubs.core.model.entity.LoginUser;
import com.muratkocoglu.inghubs.core.model.mapper.CustomerMapper;
import com.muratkocoglu.inghubs.core.model.repository.CustomerRepository;
import com.muratkocoglu.inghubs.core.model.repository.UserRepository;
import com.muratkocoglu.inghubs.core.model.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	
	private final CustomerRepository customerRepository;
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;

	@Override
	public CustomerResponseDTO getCustomerById(Long id) {
		Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return CustomerMapper.INSTANCE.toDTO(customer);
	}

	@Transactional
	@Override
	public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
		Customer savedCustomer = customerRepository.save(CustomerMapper.INSTANCE.toEntity(dto));
		
		LoginUser user = new LoginUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setCustomer(savedCustomer);

        userRepository.save(user);
		
        return CustomerMapper.INSTANCE.toDTO(savedCustomer);
	}
}
