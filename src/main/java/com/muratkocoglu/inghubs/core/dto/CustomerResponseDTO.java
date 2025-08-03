package com.muratkocoglu.inghubs.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomerResponseDTO {
	
	private Long id;
    private String name;
    private String surname;
    private String nationalId;
}
