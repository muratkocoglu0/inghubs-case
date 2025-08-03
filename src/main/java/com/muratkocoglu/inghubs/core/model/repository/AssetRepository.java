package com.muratkocoglu.inghubs.core.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.muratkocoglu.inghubs.core.model.entity.Asset;

import jakarta.persistence.LockModeType;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    
	List<Asset> findByCustomerId(Long customerId);
    
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.customer.id = :customerId AND a.name = :assetName")
	Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);
}
