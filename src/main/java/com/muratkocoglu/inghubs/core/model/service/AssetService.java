package com.muratkocoglu.inghubs.core.model.service;

import java.math.BigDecimal;
import java.util.List;

import com.muratkocoglu.inghubs.core.dto.AssetResponseDTO;

public interface AssetService {
	
	List<AssetResponseDTO> getAssets(Long customerId);
		
	void reserveBuyBalance(Long customerId, BigDecimal totalAmount);
	
    void reserveSellBalance(Long customerId, String assetName, BigDecimal quantity);
    
    void refundBuyBalance(Long customerId, BigDecimal totalAmount);
    
    void refundSellBalance(Long customerId, String assetName, BigDecimal quantity);
    
    void creditAsset(Long customerId, String assetName, BigDecimal quantity);
}
