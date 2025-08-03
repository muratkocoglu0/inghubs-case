package com.muratkocoglu.inghubs.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.muratkocoglu.inghubs.core.dto.LoginRequestDTO;
import com.muratkocoglu.inghubs.core.dto.LoginResponseDTO;
import com.muratkocoglu.inghubs.core.model.service.impl.LoginService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
	
	private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
       return loginService.login(dto);
    }
}
