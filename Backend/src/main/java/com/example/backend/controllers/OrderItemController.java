package com.example.backend.controllers;

import com.example.backend.dto.request.OrderItemRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrderItemResponse;
import com.example.backend.services.OrderItemService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/order-item")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemController {
    OrderItemService orderItemService;

    @PostMapping(value = "/{productId}")
    public ApiResponse<OrderItemResponse> addOrderItem (@PathVariable("productId") String productId, @Valid @RequestBody OrderItemRequest request) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(1000)
                .result(orderItemService.addOrderItem(request, productId))
                .build();
    }

    @GetMapping(value = "/{id}")
    public ApiResponse<OrderItemResponse> getOrderItemById (@PathVariable("id") String id) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(1000)
                .result(orderItemService.getById(id))
                .build();
    }
}
