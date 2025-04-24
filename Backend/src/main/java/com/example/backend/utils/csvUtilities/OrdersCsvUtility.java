package com.example.backend.utils.csvUtilities;

import com.example.backend.models.Customer;
import com.example.backend.models.Orders;
import com.example.backend.repositories.CustomerRepository;
import com.example.backend.repositories.OrdersRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersCsvUtility {
    OrdersRepository ordersRepository;
    CustomerRepository customerRepository;

    public List<Orders> csvToOrderList (InputStream inputStream, HttpServletResponse response) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Orders> valid = new ArrayList<>();
            List<Orders> invalid = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Orders orders = new Orders();
                orders.setId(csvRecord.get("ID") + ((isValidId(csvRecord.get("ID")) ? "" : "*")));
                orders.setOrderDate(LocalDateTime.now());
                orders.setTotalAmount(0F);
                orders.setCustomer(new Customer());
                orders.getCustomer().setId(csvRecord.get("CUSTOMER_ID") + ((isValidCustomerId(csvRecord.get("CUSTOMER_ID")) ? "" : "*")));

                if (isValidId(orders.getId()) && isValidCustomerId(orders.getCustomer().getId())) {
                    valid.add(orders);
                } else {
                    invalid.add(orders);
                }
            }
            exportInvalidList(response, invalid);

            return valid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidId (String id) {
        return id.length() == 10 && id.startsWith("ORD-") && !ordersRepository.existsById(id);
    }

    public boolean isValidCustomerId (String customerId) {
        return customerId.length() == 10 && customerId.startsWith("CTM-") && customerRepository.existsById(customerId);
    }

    public void exportInvalidList (HttpServletResponse response, List<Orders> list) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invalid_orders.csv");

        try (ServletOutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "ORDER_DATE", "TOTAL_AMOUNT", "CUSTOMER_ID"))) {
            for (Orders orders : list) {
                csvPrinter.printRecord(orders.getId(), orders.getOrderDate(), orders.getTotalAmount(), orders.getCustomer().getId());
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
