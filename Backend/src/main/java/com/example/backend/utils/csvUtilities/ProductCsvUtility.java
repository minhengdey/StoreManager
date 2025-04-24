package com.example.backend.utils.csvUtilities;

import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCsvUtility {
    ProductRepository productRepository;

    public List<Product> csvToProductList (InputStream inputStream, HttpServletResponse response) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Product> valid = new ArrayList<>();
            List<Product> invalid = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Product product = new Product();
                product.setId(csvRecord.get("ID") + ((isValidId(csvRecord.get("ID")) ? "" : "*")));
                product.setName(csvRecord.get("NAME") + ((isValidName(csvRecord.get("NAME")) ? "" : "*")));
                product.setPrice(Float.valueOf(csvRecord.get("PRICE")));
                product.setStockQuantity(Integer.valueOf(csvRecord.get("STOCK_QUANTITY")));

                if (isValidId(product.getId()) && isValidName(product.getName()) && product.getPrice() > 0 && product.getStockQuantity() > 0) {
                    valid.add(product);
                } else {
                    invalid.add(product);
                }
            }
            exportInvalidList(response, invalid);

            return valid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidId (String id) {
        return id.length() == 10 && id.startsWith("PRD-") && !productRepository.existsById(id);
    }

    public boolean isValidName (String name) {
        return name.length() >= 2 && name.length() <= 30;
    }

    public void exportInvalidList (HttpServletResponse response, List<Product> list) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invalid_products.csv");

        try (ServletOutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "NAME", "PRICE", "STOCK_QUANTITY"));) {
            for (Product product : list) {
                csvPrinter.printRecord(product.getId(), product.getName(), product.getPrice() + (product.getPrice() > 0 ? "" : "*"),
                        product.getStockQuantity() + (product.getStockQuantity() > 0 ? "" : "*"));
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
