package com.example.backend.mappers;

import com.example.backend.dto.response.OrdersResponse;
import com.example.backend.models.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrdersMapper {
    OrdersResponse toResponse (Orders orders);
}
