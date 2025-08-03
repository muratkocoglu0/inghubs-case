package com.muratkocoglu.inghubs.core.model.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.muratkocoglu.inghubs.core.dto.AssetResponseDTO;
import com.muratkocoglu.inghubs.core.model.entity.Asset;
import com.muratkocoglu.inghubs.core.model.mapper.AssetMapper;
import com.muratkocoglu.inghubs.core.model.mapper.CustomerMapper;
import com.muratkocoglu.inghubs.core.model.repository.AssetRepository;
import com.muratkocoglu.inghubs.core.model.service.AssetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
	
	private final AssetRepository assetRepository;

	@Override
	@Cacheable(value = "assets", key = "#customerId")
	public List<AssetResponseDTO> getAssets(Long customerId) {
		List<Asset> assetList = assetRepository.findByCustomerId(customerId);
		return AssetMapper.INSTANCE.toDTOList(assetList);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    @CacheEvict(value = "assets", key = "#customerId")
    public void reserveBuyBalance(Long customerId, BigDecimal totalAmount) {
        Asset tryAsset = assetRepository
                .findByCustomerIdAndAssetName(customerId, "TRY")
                .orElse(AssetMapper.INSTANCE.toNewAsset(CustomerMapper.INSTANCE.toCustomer(customerId), "TRY"));

        if (tryAsset.getUsableSize().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient TRY balance");
        }

        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(totalAmount));
        assetRepository.save(tryAsset);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    @CacheEvict(value = "assets", key = "#customerId")
    public void reserveSellBalance(Long customerId, String assetName, BigDecimal quantity) {
        Asset asset = assetRepository
                .findByCustomerIdAndAssetName(customerId, assetName)
                .orElse(AssetMapper.INSTANCE.toNewAsset(CustomerMapper.INSTANCE.toCustomer(customerId), assetName));

        if (asset.getUsableSize().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient " + assetName + " balance");
        }

        asset.setUsableSize(asset.getUsableSize().subtract(quantity));
        assetRepository.save(asset);
    }

    @Transactional
    @Override
    @CacheEvict(value = "assets", key = "#customerId")
    public void refundBuyBalance(Long customerId, BigDecimal totalAmount) {
        Asset tryAsset = assetRepository
                .findByCustomerIdAndAssetName(customerId, "TRY")
                .orElse(AssetMapper.INSTANCE.toNewAsset(CustomerMapper.INSTANCE.toCustomer(customerId), "TRY"));

        tryAsset.setUsableSize(tryAsset.getUsableSize().add(totalAmount));
        assetRepository.save(tryAsset);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    @CacheEvict(value = "assets", key = "#customerId")
    public void refundSellBalance(Long customerId, String assetName, BigDecimal quantity) {
        Asset asset = assetRepository
                .findByCustomerIdAndAssetName(customerId, assetName)
                .orElse(AssetMapper.INSTANCE.toNewAsset(CustomerMapper.INSTANCE.toCustomer(customerId), assetName));

        asset.setUsableSize(asset.getUsableSize().add(quantity));
        assetRepository.save(asset);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	@CacheEvict(value = "assets", key = "#customerId")
	public void creditAsset(Long customerId, String assetName, BigDecimal quantity) {
		Asset asset = assetRepository
	            .findByCustomerIdAndAssetName(customerId, assetName)
	            .orElse(AssetMapper.INSTANCE.toNewAsset(CustomerMapper.INSTANCE.toCustomer(customerId), assetName));

	    asset.setSize(asset.getSize().add(quantity));
	    asset.setUsableSize(asset.getUsableSize().add(quantity));

	    assetRepository.save(asset);
	}
}
