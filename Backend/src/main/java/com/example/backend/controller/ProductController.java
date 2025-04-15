package com.example.backend.controller;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping()
    public ApiResponse<ProductResponse> addProduct (@Valid @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.addProduct(request))
                .build();
    }
}
