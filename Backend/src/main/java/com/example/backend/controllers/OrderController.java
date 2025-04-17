package com.example.backend.controllers;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrdersResponse;
import com.example.backend.services.OrdersService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrdersService ordersService;

    @PostMapping(value = "/{customerId}")
    public ApiResponse<OrdersResponse> createOrder (@PathVariable("customerId") String customerId) {
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(ordersService.createOrders(customerId))
                .build();
    }

    @GetMapping(value = "/{id}")
    public ApiResponse<OrdersResponse> getById (@PathVariable("id") String id) {
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(ordersService.getById(id))
                .build();
    }
}
