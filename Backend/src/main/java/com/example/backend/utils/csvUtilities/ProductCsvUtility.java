package com.example.backend.utils.csvUtilities;

import com.example.backend.models.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProductCsvUtility {
    public static List<Product> csvToProductList (InputStream inputStream, HttpServletResponse response) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Product> valid = new ArrayList<>();
            List<Product> invalid = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Product product = new Product();
                product.setId(csvRecord.get("ID"));
                product.setName(csvRecord.get("NAME"));
                product.setPrice(Float.valueOf(csvRecord.get("PRICE")));
                product.setStockQuantity(Integer.valueOf(csvRecord.get("STOCK_QUANTITY")));

                if (isValidateId(product.getId()) && isValidateName(product.getName()) && product.getPrice() > 0 && product.getStockQuantity() > 0) {
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

    public static boolean isValidateId (String id) {
        return id.length() == 10 && id.startsWith("PRD-");
    }

    public static boolean isValidateName (String name) {
        return name.length() >= 2 && name.length() <= 30;
    }

    public static void exportInvalidList (HttpServletResponse response, List<Product> list) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invalid_products.csv");

        try (ServletOutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "NAME", "PRICE", "STOCK_QUANTITY"));) {
            for (Product product : list) {
                csvPrinter.printRecord(product.getId(), product.getName(), product.getPrice(), product.getStockQuantity());
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
