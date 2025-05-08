package com.example.backend.controllers;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.services.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping(value = "/product")
    public ApiResponse<ProductResponse> addProduct (@Valid @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.addProduct(request))
                .build();
    }

    @GetMapping(value = "/product/by-id/{id}")
    public ApiResponse<ProductResponse> findById (@PathVariable("id") String id) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.findById(id))
                .build();
    }

    @GetMapping(value = "/product/by-name/{name}")
    public ApiResponse<ProductResponse> findByName (@PathVariable("name") String name) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.findByName(name))
                .build();
    }

    @PutMapping(value = "/product/{id}")
    public ApiResponse<ProductResponse> updateById (@PathVariable("id") String id, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .code(1000)
                .result(productService.updateProduct(request, id))
                .build();
    }

    @DeleteMapping(value = "/product/{id}")
    public void deleteByID (@PathVariable("id") String id) {
        productService.deleteProduct(id);
    }

    @GetMapping(value = "/products")
    public ApiResponse<List<ProductResponse>> getAll (@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        return ApiResponse.<List<ProductResponse>>builder()
                .code(1000)
                .result(productService.getAllProduct(page, pageSize))
                .build();
    }
}
