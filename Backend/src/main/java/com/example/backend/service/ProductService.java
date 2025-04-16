package com.example.backend.service;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exception.AppException;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.util.IdGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    public ProductResponse addProduct (ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }
        Product product = productMapper.toProduct(request);
        String id = IdGenerator.generateId("PRD");
        while (productRepository.existsById(id)) {
            id = IdGenerator.generateId("PRD");
        }
        product.setId(id);
        return productMapper.toResponse(productRepository.addProduct(product));
    }

    public ProductResponse findById (String id) {
        return productMapper.toResponse(productRepository.findById(id));
    }

    public ProductResponse findByName (String name) {
        return productMapper.toResponse(productRepository.findByName(name));
    }
}
