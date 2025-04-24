package com.example.backend.utils.csv;

import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
import com.example.backend.models.Product;
import com.example.backend.repositories.OrderItemRepository;
import com.example.backend.repositories.OrdersRepository;
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
public class OrderItemCsv {
    OrderItemRepository orderItemRepository;
    ProductRepository productRepository;
    OrdersRepository ordersRepository;

    public List<OrderItem> csvToOrderItem (InputStream inputStream, HttpServletResponse response) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<OrderItem> valid = new ArrayList<>();
            List<OrderItem> invalid = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(csvRecord.get("ID") + ((isValidId(csvRecord.get("ID")) ? "" : "*")));
                orderItem.setQuantity(Integer.valueOf(csvRecord.get("QUANTITY")));
                orderItem.setProduct(new Product());
                orderItem.getProduct().setId(csvRecord.get("PRODUCT_ID") + ((isValidProductId(csvRecord.get("PRODUCT_ID")) ? "" : "*")));
                orderItem.setOrders(new Orders());
                orderItem.getOrders().setId(csvRecord.get("ORDERS_ID") + ((isValidOrdersId(csvRecord.get("ORDERS_ID")) ? "" : "*")));

                if (isValidId(orderItem.getId()) && orderItem.getQuantity() > 0 && isValidProductId(orderItem.getProduct().getId()) &&
                        isValidOrdersId(orderItem.getOrders().getId())) {
                    valid.add(orderItem);
                } else {
                    invalid.add(orderItem);
                }
            }
            exportInvalidList(response, invalid);

            return valid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidId (String id) {
        return id.length() == 10 && id.startsWith("ORI-") && !orderItemRepository.existsById(id);
    }

    public boolean isValidProductId (String productId) {
        return productId.length() == 10 && productId.startsWith("PRD-") && productRepository.existsById(productId);
    }

    public boolean isValidOrdersId (String ordersId) {
        return ordersId.length() == 10 && ordersId.startsWith("ORD-") && ordersRepository.existsById(ordersId);
    }

    public void exportInvalidList (HttpServletResponse response, List<OrderItem> list) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invalid_order_items.csv");

        try (ServletOutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "QUANTITY", "PRODUCT_ID", "ORDERS_ID"))) {
            for (OrderItem orderItem : list) {
                csvPrinter.printRecord(orderItem.getId(), orderItem.getQuantity() + (orderItem.getQuantity() > 0 ? "" : "*"),
                        orderItem.getProduct().getId(), orderItem.getOrders().getId());
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
