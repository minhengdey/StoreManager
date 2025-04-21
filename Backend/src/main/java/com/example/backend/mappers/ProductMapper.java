package com.example.backend.mappers;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    Product toProduct (ProductRequest request);
    ProductResponse toResponse (Product product);
    void update (@MappingTarget Product product, ProductRequest request);
}
