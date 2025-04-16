package com.example.backend.controller;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.services.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/by-id/{id}")
    public ApiResponse<ProductResponse> findById (@PathVariable("id") String id) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.findById(id))
                .build();
    }

    @GetMapping(value = "/by-name/{name}")
    public ApiResponse<ProductResponse> findByName (@PathVariable("name") String name) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.findByName(name))
                .build();
    }

    @PutMapping(value = "/{id}")
    public ApiResponse<ProductResponse> updateById (@PathVariable("id") String id, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.update(request, id))
                .build();
    }
}
