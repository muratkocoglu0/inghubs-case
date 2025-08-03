package com.muratkocoglu.inghubs.core.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AssetResponseDTO {

	private Long id;
    private Long customerId;
    private String name;
    private BigDecimal size;
    private BigDecimal usableSize;
}
