package com.muratkocoglu.inghubs.core.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AssetRequestDTO {
	
	private String assetName;
    private BigDecimal size;
    private BigDecimal usableSize;
}
