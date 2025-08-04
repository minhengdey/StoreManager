package com.example.backend.controllers;

import com.example.backend.dto.request.OrderItemRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrderItemResponse;
import com.example.backend.services.OrderItemService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemController {
    OrderItemService orderItemService;

    @PostMapping(value = "/order-item/{productId}/{ordersId}")
    public ApiResponse<OrderItemResponse> addOrderItem (@PathVariable("productId") String productId,
                                                        @PathVariable("ordersId") String ordersId,
                                                        @Valid @RequestBody OrderItemRequest request) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(1000)
                .result(orderItemService.addOrderItem(request, productId, ordersId))
                .build();
    }

    @GetMapping(value = "/order-item/{id}")
    public ApiResponse<OrderItemResponse> getOrderItemById (@PathVariable("id") String id) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(1000)
                .result(orderItemService.getById(id))
                .build();
    }

    @PutMapping(value = "/order-item/{id}")
    public ApiResponse<OrderItemResponse> updateOrderItem (@Valid @RequestBody OrderItemRequest request, @PathVariable("id") String id) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(1000)
                .result(orderItemService.updateOrderItem(request, id))
                .build();
    }

    @DeleteMapping(value = "/order-item/{id}")
    public void deleteOrderItem (@PathVariable("id") String id) {
        orderItemService.deleteOrderItem(id);
    }

    @GetMapping(value = "/order-items/by-productId/{productId}")
    public ApiResponse<List<OrderItemResponse>> getAllByProductId (@PathVariable("productId") String productId, @RequestParam("page") int page,
                                                                   @RequestParam("pageSize") int pageSize) {
        return ApiResponse.<List<OrderItemResponse>>builder()
                .code(1000)
                .result(orderItemService.getAllByProductId(productId, page, pageSize))
                .build();
    }

    @PostMapping(value = "/upload")
    public void uploadFile (@RequestParam("file")MultipartFile file, HttpServletResponse response) throws IOException {
        orderItemService.saveAllFromFile(file, response);
    }
}
