package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import org.springframework.batch.item.ItemProcessor;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product product) {
        String name = product.getName().toUpperCase();

        return new Product(product.getId(), name, product.getPrice(), product.getStockQuantity());
    }
}
