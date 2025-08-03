package com.muratkocoglu.inghubs.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDTO {
	
	@NotNull(message = "Username must not be empty")
	private String username;
	
	@NotNull(message = "Password must not be empty")
	private String password;

	@NotNull(message = "Name must not be empty")
	private String name;
    
	@NotNull(message = "Surname must not be empty")
	private String surname;
    
	@NotNull(message = "National ID must not be empty")
	@Size(min = 11, max = 11, message = "National ID must be exactly 11 characters long")
    @Pattern(regexp = "\\d+", message = "National ID must contain only digits")
	private String nationalId;
}
