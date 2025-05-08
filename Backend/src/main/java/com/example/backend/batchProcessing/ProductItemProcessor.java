package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import jakarta.xml.bind.ValidationException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductItemProcessor implements ItemProcessor<Product, Product> {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product process(Product product) throws Exception {
        if (isValidId(product.getId()) && isValidName(product.getName()) && product.getPrice() > 0 && product.getStockQuantity() > 0) {
            String name = product.getName().toUpperCase();

            return new Product(product.getId(), name, product.getPrice(), product.getStockQuantity());
        }
        throw new ValidationException("Invalid product");
    }

    public boolean isValidId (String id) {
        return id.length() == 10 && id.startsWith("PRD-") && !productRepository.existsById(id);
    }

    public boolean isValidName (String name) {
        return name.length() >= 2 && name.length() <= 30;
    }
}
