package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import jakarta.validation.ValidationException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductItemProcessor implements ItemProcessor<Product, Product> {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product process(Product product) {
        // validation dữ liệu đầu vào, nếu không thỏa mãn thì bắn ra ValidationException để bên SkipListener lắng nghe, skip và cho vào file invalid product
        String message = "";
        if (!isValidId(product.getId())) {
            message = "Invalid id";
        } else if (!isValidName(product.getName())) {
            message = "Invalid name";
        } else if (product.getPrice() <= 0) {
            message = "Invalid price";
        } else if (product.getStockQuantity() <= 0){
            message = "Invalid stock quantity";
        } else {
            String name = product.getName().toUpperCase();
            return new Product(product.getId(), name, product.getPrice(), product.getStockQuantity());
        }
        throw new ValidationException(message);
    }

    public synchronized boolean isValidId (String id) {
        return id.length() == 10 && id.startsWith("PRD-") && !productRepository.existsById(id);
    }

    public synchronized boolean isValidName (String name) {
        return name.length() >= 2 && name.length() <= 30;
    }
}
