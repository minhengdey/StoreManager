package com.example.backend.utils.excelUtilities;

import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Customer;
import com.example.backend.models.Orders;
import com.example.backend.repositories.OrdersRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersExcelUtility {
    OrdersRepository ordersRepository;

    public List<Orders> excelToOrdersList (InputStream inputStream, HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet("Orders");

            if (sheet == null) {
                throw new AppException(ErrorCode.SHEET_NOT_FOUND);
            }

            Iterator<Row> rows = sheet.iterator();
            List<Orders> valid = new ArrayList<>();
            List<Orders> invalid = new ArrayList<>();
            int rowNumbers = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumbers == 0) {
                    ++ rowNumbers;
                    continue;
                }

                Iterator<Cell> cells = currentRow.iterator();
                int cellNumbers = 0;
                boolean isValid = true;
                Orders orders = new Orders();
                orders.setTotalAmount(0F);
                orders.setOrderDate(LocalDateTime.now());

                while (cells.hasNext()) {
                    Cell currentCell = cells.next();

                    if (cellNumbers == 0) {
                        isValid &= isValidId(currentCell);
                        orders.setId(currentCell.getStringCellValue());
                    } else {
                        orders.setCustomer(new Customer());
                        orders.getCustomer().setId(currentCell.getStringCellValue());
                    }

                    ++ cellNumbers;
                }

                if (isValid) {
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

    public boolean isValidId (Cell cell) {
        if (!cell.getCellType().equals(CellType.STRING) || cell.getStringCellValue().length() != 10) {
            return false;
        }
        String s = cell.getStringCellValue().substring(0, 4);
        return s.equals("ORD-");
    }

    public void exportInvalidList (HttpServletResponse response, List<Orders> list) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("InvalidOrders");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("CUSTOMER_ID");

        int rowNumbers = 1;
        for (Orders orders : list) {
            Row row = sheet.createRow(rowNumbers ++);
            row.createCell(0).setCellValue(orders.getId());
            row.createCell(1).setCellValue(orders.getCustomer().getId());
        }

        for (int i = 0; i < 2; ++ i) {
            sheet.autoSizeColumn(i);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=invalid_orders.xlsx");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
