package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class ProductCsvSkipListener implements SkipListener<Product, Product> {
    final Path invalidFilePath = Paths.get("invalid_products.csv");

    @Override
    public void onSkipInProcess(Product item, Throwable t) {
        // tạo file csv để điền các product invalid
        try (BufferedWriter writer = Files.newBufferedWriter(invalidFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(String.format("%s,%s,%s,%s,INVALID\n",
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getStockQuantity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSkipInWrite(Product item, Throwable t) {
        try {
            // catch luôn exception từ onSkipInProcess
            onSkipInProcess(item, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        SkipListener.super.onSkipInRead(t);
    }
}
