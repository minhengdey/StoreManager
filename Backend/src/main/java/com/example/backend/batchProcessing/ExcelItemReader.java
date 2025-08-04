package com.example.backend.batchProcessing;

import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Customer;
import com.example.backend.models.Product;
import com.example.backend.utils.excel.CustomerExcel;
import com.example.backend.utils.excel.ProductExcel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelItemReader implements ItemReader<Product> {

    private final Iterator<Product> productIterator;

    public ExcelItemReader(InputStream inputStream) {
        List<Product> products = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheet("Product");
            if (sheet == null) {
                throw new AppException(ErrorCode.SHEET_NOT_FOUND);
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Product product = new Product();

                product.setId(row.getCell(0).getStringCellValue());
                product.setName(row.getCell(1).getStringCellValue());
                product.setPrice((float) row.getCell(2).getNumericCellValue());
                product.setStockQuantity((int) row.getCell(3).getNumericCellValue());

                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.productIterator = products.iterator();
    }

    @Override
    public Product read() {
        return productIterator.hasNext() ? productIterator.next() : null;
    }
}
