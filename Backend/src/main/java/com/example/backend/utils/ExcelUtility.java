package com.example.backend.utils;

import com.example.backend.models.Customer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtility {

    public static List<Customer> excelToCustomerList (InputStream inputStream) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet("customer");
            Iterator<Row> rows = sheet.iterator();

            List<Customer> list = new ArrayList<>();

            int rowNumbers = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumbers == 0) {
                    ++ rowNumbers;
                    continue;
                }

                Iterator<Cell> cells = currentRow.iterator();
                Customer customer = new Customer();
                int cellNumbers = 0;
                while (cells.hasNext()) {
                    Cell currentCell = cells.next();
                    if (cellNumbers == 0) {
                        customer.setId(currentCell.getStringCellValue());
                    } else if (cellNumbers == 1) {
                        customer.setName(currentCell.getStringCellValue());
                    } else if (cellNumbers == 2) {
                        customer.setName(currentCell.getStringCellValue());
                    } else {
                        customer.setName(currentCell.getStringCellValue());
                    }
                    ++ cellNumbers;
                }

                list.add(customer);
            }
            workbook.close();
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
