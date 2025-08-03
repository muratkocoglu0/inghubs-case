package com.muratkocoglu.inghubs.core.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderRequestDTO {
	private Long customerId;
	private String assetName;
	private OrderSide side;
	private BigDecimal size;
	private BigDecimal price;
}
