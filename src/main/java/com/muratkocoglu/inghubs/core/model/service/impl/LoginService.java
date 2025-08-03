package com.muratkocoglu.inghubs.core.model.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.muratkocoglu.inghubs.core.dto.CustomUserDetails;
import com.muratkocoglu.inghubs.core.dto.LoginRequestDTO;
import com.muratkocoglu.inghubs.core.dto.LoginResponseDTO;
import com.muratkocoglu.inghubs.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO dto) {
    	
    	try {
	        Authentication authentication = authManager.authenticate(
	                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
	        );
	
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        
	        String token = jwtUtil.generateToken(
	                userDetails.getUsername(),
	                userDetails.getRole(),
	                userDetails.getCustomerId()
	        );
	
	        return LoginResponseDTO.builder()
	            	.accessToken(token)
	            	.expiresAt(jwtUtil.extractExpiration(token))
	            	.username(dto.getUsername())
	            	.role(userDetails.getRole())
	            	.build();
	    } 
	    catch (AuthenticationException e) {
	        throw new RuntimeException("Invalid username or password");
	    }
    }
}
