package com.muratkocoglu.inghubs.core.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.muratkocoglu.inghubs.core.dto.CustomerRequestDTO;
import com.muratkocoglu.inghubs.core.dto.CustomerResponseDTO;
import com.muratkocoglu.inghubs.core.dto.UserRole;
import com.muratkocoglu.inghubs.core.model.entity.Customer;
import com.muratkocoglu.inghubs.core.model.entity.LoginUser;
import com.muratkocoglu.inghubs.core.model.repository.CustomerRepository;
import com.muratkocoglu.inghubs.core.model.repository.UserRepository;
import com.muratkocoglu.inghubs.core.model.service.impl.CustomerServiceImpl;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");

        requestDTO = new CustomerRequestDTO();
        requestDTO.setName("John");
        requestDTO.setSurname("Doe");
        requestDTO.setUsername("john.doe");
        requestDTO.setPassword("123456");
    }

    @Test
    void getCustomerById_shouldReturnCustomer_whenExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDTO response = customerService.getCustomerById(1L);

        assertNotNull(response);
        assertEquals("John", response.getName());
        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_shouldThrowException_whenNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerService.getCustomerById(1L));

        assertTrue(ex.getMessage().contains("Customer not found"));
        verify(customerRepository).findById(1L);
    }

    @Test
    void createCustomer_shouldSaveCustomerAndUser() {
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        when(passwordEncoder.encode("123456")).thenReturn("encodedPass");

        CustomerResponseDTO response = customerService.createCustomer(requestDTO);

        assertNotNull(response);
        assertEquals("John", response.getName());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, times(1)).save(any(LoginUser.class));
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void createCustomer_shouldAssignRoleCustomer() {
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");

        customerService.createCustomer(requestDTO);

        ArgumentCaptor<LoginUser> userCaptor = ArgumentCaptor.forClass(LoginUser.class);
        verify(userRepository).save(userCaptor.capture());

        LoginUser savedUser = userCaptor.getValue();
        assertEquals(UserRole.CUSTOMER, savedUser.getRole());
        assertEquals(customer, savedUser.getCustomer());
    }
}
