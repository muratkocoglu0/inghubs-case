package com.muratkocoglu.inghubs.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.muratkocoglu.inghubs.core.configuration.CheckCustomerAccess;
import com.muratkocoglu.inghubs.core.dto.AssetResponseDTO;
import com.muratkocoglu.inghubs.core.dto.RestResponse;
import com.muratkocoglu.inghubs.core.model.service.impl.AssetServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetServiceImpl assetService;
    
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @CheckCustomerAccess("customerId")
    public RestResponse<List<AssetResponseDTO>> getAssets(@PathVariable Long customerId) {
        return RestResponse.of(assetService.getAssets(customerId), null, null);
    }
}
