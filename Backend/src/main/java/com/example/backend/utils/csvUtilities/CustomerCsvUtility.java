package com.example.backend.utils.csvUtilities;

import com.example.backend.models.Customer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerCsvUtility {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{10}$");

    public static List<Customer> csvToCustomerList (InputStream inputStream, HttpServletResponse response) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Customer> valid = new ArrayList<>();
            List<Customer> invalid = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Customer customer = new Customer();
                customer.setId(csvRecord.get("ID"));
                customer.setName(csvRecord.get("NAME"));
                customer.setPhone(csvRecord.get("PHONE"));
                customer.setEmail(csvRecord.get("EMAIL"));

                if (isValidateId(customer.getId()) && isValidateName(customer.getName()) && isValidatePhone(customer.getPhone())
                        && isValidateEmail(customer.getEmail())) {
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

    public static boolean isValidateId (String id) {
        return id.length() == 10 && id.startsWith("CTM-");
    }

    public static boolean isValidateName (String name) {
        return name.length() >= 2 && name.length() <= 30;
    }

    public static boolean isValidatePhone (String phone) {
        return PHONE_REGEX.matcher(phone).matches();
    }

    public static boolean isValidateEmail (String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    public static void exportInvalidList (HttpServletResponse response, List<Customer> list) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invalid_customers.csv");

        try (ServletOutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "NAME", "PHONE", "EMAIL"));) {
            for (Customer customer : list) {
                csvPrinter.printRecord(customer.getId(), customer.getName(), customer.getPhone(), customer.getEmail());
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
