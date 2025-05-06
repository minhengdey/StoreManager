package com.example.backend.services;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.ProductMapper;
import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    ProductRequest request;
    String id;

    @BeforeEach
    void setup () {
        request = new ProductRequest("Keo", 10.5F, 20);
        id = "PRD-123AAA";
    }

    @Test
    void addProduct_ShouldReturnResponse () {
        when(productRepository.existsByName(request.getName())).thenReturn(false);

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        when(productMapper.toProduct(request)).thenReturn(product);

        try (MockedStatic<IdGenerator> mockIdGen = Mockito.mockStatic(IdGenerator.class)) {
            mockIdGen.when(() -> IdGenerator.generateId("PRD")).thenReturn(id);

            when(productRepository.existsById(id)).thenReturn(false);
            product.setId(id);

            when(productRepository.addProduct(product)).thenReturn(product);

            ProductResponse expected = new ProductResponse(product.getId(), product.getName(),
                    product.getPrice(), product.getStockQuantity());
            when(productMapper.toResponse(product)).thenReturn(expected);

            ProductResponse actual = productService.addProduct(request);

            assertEquals(expected, actual);
            verify(productRepository).addProduct(product);
        }
    }

    @Test
    void addProduct_ExistsByName_ThrowAppException () {
        when(productRepository.existsByName(request.getName())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> productService.addProduct(request));

        assertEquals(ErrorCode.PRODUCT_EXISTED, exception.getErrorCode());
    }

    @Test
    void findById_ShouldReturnResponse () {
        Product product = new Product(id, request.getName(), request.getPrice(), request.getStockQuantity());
        when(productRepository.findById(id)).thenReturn(product);

        ProductResponse expected = new ProductResponse(product.getId(), product.getName(),
                product.getPrice(), product.getStockQuantity());
        when(productMapper.toResponse(product)).thenReturn(expected);

        ProductResponse actual = productService.findById(id);

        assertEquals(expected, actual);
        verify(productRepository).findById(id);
    }

    @Test
    void findById_NotFound_ThrowAppException () {
        when(productRepository.findById(id)).thenThrow(new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        AppException exception = assertThrows(AppException.class,
                () -> productRepository.findById(id));

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }
}
