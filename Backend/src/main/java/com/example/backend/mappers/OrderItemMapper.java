package com.example.backend.mappers;

import com.example.backend.dto.request.OrderItemRequest;
import com.example.backend.dto.response.OrderItemResponse;
import com.example.backend.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toOrderItem (OrderItemRequest request);
    OrderItemResponse toResponse (OrderItem orderItem);
    void update (@MappingTarget OrderItem orderItem, OrderItemRequest request);
}
