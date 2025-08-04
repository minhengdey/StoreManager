package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import jakarta.validation.ValidationException;
import lombok.Getter;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class ProductExcelSkipListener implements SkipListener<Product, Product> {
    final List<Product> invalidProducts = new ArrayList<>();

    @Override
    public void onSkipInProcess(Product item, Throwable t) {
        if (t instanceof ValidationException) {
            item.setImportMessage(t.getMessage());
            invalidProducts.add(item);
        } else {
            SkipListener.super.onSkipInProcess(item, t);
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        SkipListener.super.onSkipInRead(t);
    }
}
