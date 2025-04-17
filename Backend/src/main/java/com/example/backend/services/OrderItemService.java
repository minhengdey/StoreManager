package com.example.backend.services;

import com.example.backend.dto.request.OrderItemRequest;
import com.example.backend.dto.response.OrderItemResponse;
import com.example.backend.mappers.OrderItemMapper;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Product;
import com.example.backend.repositories.OrderItemRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {

    OrderItemRepository orderItemRepository;
    OrderItemMapper orderItemMapper;
    ProductRepository productRepository;

    public OrderItemResponse addOrderItem (OrderItemRequest request, String productId) {
        Product product = productRepository.findById(productId);
        OrderItem orderItem = orderItemMapper.toOrderItem(request);
        orderItem.setProduct(product);

        String id = IdGenerator.generateId("ORI");
        while (orderItemRepository.existsById(id)) {
            id = IdGenerator.generateId("ORI");
        }
        orderItem.setId(id);

        return orderItemMapper.toResponse(orderItemRepository.addOrderItem(orderItem));
    }
}
