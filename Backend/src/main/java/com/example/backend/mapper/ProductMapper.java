package com.example.backend.mapper;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct (ProductRequest request);
    ProductResponse toResponse (Product product);
}
