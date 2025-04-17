package com.example.backend.mappers;

import com.example.backend.dto.response.OrdersResponse;
import com.example.backend.models.Orders;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdersMapper {
    OrdersResponse toResponse (Orders orders);
}
