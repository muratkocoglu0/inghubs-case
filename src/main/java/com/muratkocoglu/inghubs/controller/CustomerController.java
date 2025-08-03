package com.muratkocoglu.inghubs.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.muratkocoglu.inghubs.core.configuration.CheckCustomerAccess;
import com.muratkocoglu.inghubs.core.dto.CustomerRequestDTO;
import com.muratkocoglu.inghubs.core.dto.CustomerResponseDTO;
import com.muratkocoglu.inghubs.core.dto.RestResponse;
import com.muratkocoglu.inghubs.core.model.service.impl.CustomerServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerServiceImpl customerService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @CheckCustomerAccess("id")
    public RestResponse<CustomerResponseDTO> getCustomer(@PathVariable Long id) {
        return RestResponse.of(customerService.getCustomerById(id), null, null);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public RestResponse<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO dto) {
        return RestResponse.of(customerService.createCustomer(dto), null, null);
    }

}
