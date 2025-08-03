package com.muratkocoglu.inghubs.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
	
	@NotNull(message = "Username must not be empty")
	private String username;
	
    @NotNull(message = "Password must not be empty")
	private String password;

}
