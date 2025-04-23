package com.example.backend.utils.excelUtilities;

import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
import com.example.backend.models.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderItemExcelUtility {

    public static List<OrderItem> excelToOrderItemList (InputStream inputStream, HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet("OrderItem");

            if (sheet == null) {
                throw new AppException(ErrorCode.SHEET_NOT_FOUND);
            }

            Iterator<Row> rows = sheet.iterator();

            List<OrderItem> valid = new ArrayList<>();
            List<OrderItem> invalid = new ArrayList<>();

            int rowNumbers = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumbers == 0) {
                    ++ rowNumbers;
                    continue;
                }

                Iterator<Cell> cells = currentRow.iterator();
                OrderItem orderItem = new OrderItem();
                int cellNumbers = 0;
                boolean isValid = true;

                while (cells.hasNext()) {
                    Cell currentCell = cells.next();

                    if (cellNumbers == 0) {
                        isValid &= isValidId(currentCell);
                        orderItem.setId(currentCell.getStringCellValue());
                    } else if (cellNumbers == 1) {
                        isValid &= isValidQuantity(currentCell);
                        orderItem.setQuantity((int) currentCell.getNumericCellValue());
                    } else if (cellNumbers == 2) {
                        isValid &= ProductExcelUtility.isValidId(currentCell);
                        orderItem.setProduct(new Product());
                        orderItem.getProduct().setId(currentCell.getStringCellValue());
                    } else {
//                        isValid &=
                        orderItem.setOrders(new Orders());
                        orderItem.getOrders().setId(currentCell.getStringCellValue());
                    }
                    ++ cellNumbers;
                }

                if (isValid) {
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

    public static boolean isValidId (Cell cell) {
        if (!cell.getCellType().equals(CellType.STRING) || cell.getStringCellValue().length() != 10) {
            return false;
        }
        String s = cell.getStringCellValue().substring(0, 4);
        return s.equals("ORI-");
    }

    public static boolean isValidQuantity (Cell cell) {
        return (cell.getCellType().equals(CellType.NUMERIC) && cell.getNumericCellValue() > 0);
    }

    public static void exportInvalidList (HttpServletResponse response, List<OrderItem> list) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("InvalidOrderItem");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("QUANTITY");
        header.createCell(2).setCellValue("PRODUCT_ID");
        header.createCell(3).setCellValue("ORDERS_ID");

        int rowNumbers = 1;
        for (OrderItem orderItem : list) {
            Row row = sheet.createRow(rowNumbers ++);
            row.createCell(0).setCellValue(orderItem.getId());
            row.createCell(1).setCellValue(orderItem.getQuantity());
            row.createCell(2).setCellValue(orderItem.getProduct().getId());
            row.createCell(3).setCellValue(orderItem.getOrders().getId());
        }

        for (int i = 0; i < 4; ++ i) {
            sheet.autoSizeColumn(i);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=invalid_order_items.xlsx");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
