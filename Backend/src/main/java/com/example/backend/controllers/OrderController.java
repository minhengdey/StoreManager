package com.example.backend.controllers;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrdersResponse;
import com.example.backend.services.OrdersService;
import jakarta.servlet.http.HttpServletResponse;
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
public class OrderController {
    OrdersService ordersService;

    @PostMapping(value = "/order/{customerId}")
    public ApiResponse<OrdersResponse> createOrder (@PathVariable("customerId") String customerId) {
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(ordersService.createOrders(customerId))
                .build();
    }

    @GetMapping(value = "/order/{id}")
    public ApiResponse<OrdersResponse> getById (@PathVariable("id") String id) {
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(ordersService.getById(id))
                .build();
    }

    @DeleteMapping(value = "/order/{id}")
    public void deleteOrder (@PathVariable("id") String id) {
        ordersService.deleteOrders(id);
    }

    @GetMapping(value = "/orders")
    public ApiResponse<List<OrdersResponse>> getAllOrders (@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        return ApiResponse.<List<OrdersResponse>>builder()
                .code(1000)
                .result(ordersService.getAllOrders(page, pageSize))
                .build();
    }

    @PostMapping(value = "/upload")
    public void uploadFile (@RequestParam("file")MultipartFile file, HttpServletResponse response) throws IOException {
        ordersService.saveAllFromFile(file, response);
    }
}
