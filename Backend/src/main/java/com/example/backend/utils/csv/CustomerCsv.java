package com.example.backend.utils.csv;

import com.example.backend.models.Customer;
import com.example.backend.repositories.CustomerRepository;
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
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerCsv {
    Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    Pattern PHONE_REGEX = Pattern.compile("^\\d{10}$");

    CustomerRepository customerRepository;

    public List<Customer> csvToCustomerList (InputStream inputStream, HttpServletResponse response) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Customer> valid = new ArrayList<>();
            List<Customer> invalid = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                boolean[] cellMark = new boolean[4];
                Customer customer = new Customer();

                customer.setId(csvRecord.get("ID") + ((isValidId(csvRecord.get("ID")) ? "" : "*")));
                customer.setName(csvRecord.get("NAME") + ((isValidName(csvRecord.get("NAME")) ? "" : "*")));
                customer.setPhone(csvRecord.get("PHONE") + ((isValidPhone(csvRecord.get("PHONE")) ? "" : "*")));
                customer.setEmail(csvRecord.get("EMAIL") + ((isValidEmail(csvRecord.get("EMAIL")) ? "" : "*")));

                if (isValidId(customer.getId()) && isValidName(customer.getName()) && isValidPhone(customer.getPhone())
                        && isValidEmail(customer.getEmail())) {
                    valid.add(customer);
                } else {
                    invalid.add(customer);
                }
            }

            exportInvalidList(response, invalid);
            return valid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidId (String id) {
        return id.length() == 10 && id.startsWith("CTM-") && !customerRepository.existsById(id);
    }

    public boolean isValidName (String name) {
        return name.length() >= 2 && name.length() <= 30;
    }

    public boolean isValidPhone (String phone) {
        return PHONE_REGEX.matcher(phone).matches();
    }

    public boolean isValidEmail (String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    public void exportInvalidList (HttpServletResponse response, List<Customer> list) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invalid_customers.csv");

        try (ServletOutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "NAME", "PHONE", "EMAIL"))) {
            for (Customer customer : list) {
                csvPrinter.printRecord(customer.getId(), customer.getName(), customer.getPhone(), customer.getEmail());
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
