package com.example.backend.services;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.enums.FileType;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.ProductMapper;
import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.utils.FileUtility;
import com.example.backend.utils.IdGenerator;
import com.example.backend.utils.csvUtilities.ProductCsvUtility;
import com.example.backend.utils.excelUtilities.ProductExcelUtility;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductExcelUtility productExcelUtility;
    ProductCsvUtility productCsvUtility;

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

    public ProductResponse updateProduct (ProductRequest request, String id) {
        Product product = productRepository.findById(id);
        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }
        productMapper.update(product, request);
        return productMapper.toResponse(productRepository.saveProduct(product));
    }

    public void deleteProduct (String id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteProduct(id);
    }

    public List<ProductResponse> getAllProduct () {
        return productRepository.getAllProduct().stream().map(productMapper::toResponse).toList();
    }

    public void saveAllFromFile (MultipartFile file, HttpServletResponse response) throws IOException {
        if (FileUtility.getFileType(file).equals(FileType.EXCEL)) {
            List<Product> list = productExcelUtility.excelToProductList(file.getInputStream(), response);
            productRepository.saveAll(list);
        } else if (FileUtility.getFileType(file).equals(FileType.CSV)) {
            List<Product> list = productCsvUtility.csvToProductList(file.getInputStream(), response);
            productRepository.saveAll(list);
        } else {
            throw new AppException(ErrorCode.UNKNOWN_FILE_TYPE);
        }
    }
}
