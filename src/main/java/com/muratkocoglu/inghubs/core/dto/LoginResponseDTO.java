package com.muratkocoglu.inghubs.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

	private String accessToken;
    private long expiresAt;
    private String username;
    private String role;
}
