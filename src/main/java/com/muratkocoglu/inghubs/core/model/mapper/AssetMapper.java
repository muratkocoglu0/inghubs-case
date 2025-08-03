package com.muratkocoglu.inghubs.core.model.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.muratkocoglu.inghubs.core.dto.AssetResponseDTO;
import com.muratkocoglu.inghubs.core.model.entity.Asset;
import com.muratkocoglu.inghubs.core.model.entity.Customer;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class}, imports = {BigDecimal.class})
public interface AssetMapper {
	
	AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);
	
	@Mapping(target = "customerId", source = "customer.id")
	AssetResponseDTO toDto(Asset asset);
	
	@Mapping(target = "name", source = "assetName")
    @Mapping(target = "customer", source = "customer")
	@Mapping(target = "size", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "usableSize", expression = "java(BigDecimal.ZERO)")
    Asset toNewAsset(Customer customer, String assetName);
	
	List<AssetResponseDTO> toDTOList(List<Asset> assets);
	
}
