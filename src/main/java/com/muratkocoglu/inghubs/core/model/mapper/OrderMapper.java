package com.muratkocoglu.inghubs.core.model.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.muratkocoglu.inghubs.core.dto.OrderRequestDTO;
import com.muratkocoglu.inghubs.core.dto.OrderResponseDTO;
import com.muratkocoglu.inghubs.core.model.entity.Order;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class}, imports = {LocalDateTime.class})
public interface OrderMapper {
	
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "customer.id", source = "customerId")
    Order toEntity(OrderRequestDTO request);

	@Mapping(target = "customerId", source = "customer.id")
    OrderResponseDTO toDto(Order order);
    
    List<OrderResponseDTO> toDTOList(List<Order> orders);
    
}
