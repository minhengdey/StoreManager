package com.example.backend.services;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.mappers.ProductMapper;
import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductMapper productMapper;
    @InjectMocks
    ProductService productService;

    @Test
    void addProduct_ShouldReturnResponse () {
        ProductRequest request = new ProductRequest("Keo", 10.5F, 20);
        when(productRepository.existsByName(request.getName())).thenReturn(false);

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        when(productMapper.toProduct(request)).thenReturn(product);

        try (MockedStatic<IdGenerator> mockIdGen = Mockito.mockStatic(IdGenerator.class)) {
            mockIdGen.when(() -> IdGenerator.generateId("PRD")).thenReturn("PRD-123AAA");

            when(productRepository.existsById("PRD-123AAA")).thenReturn(false);
            product.setId("PRD-123AAA");

            when(productRepository.addProduct(product)).thenReturn(product);

            ProductResponse expected = new ProductResponse(product.getId(), product.getName(),
                    product.getPrice(), product.getStockQuantity());
            when(productMapper.toResponse(product)).thenReturn(expected);

            ProductResponse actual = productService.addProduct(request);

            assertEquals(expected, actual);
            verify(productRepository).addProduct(product);
        }
    }
}
