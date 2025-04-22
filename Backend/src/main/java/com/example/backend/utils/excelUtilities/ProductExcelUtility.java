package com.example.backend.utils.excelUtilities;

import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
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

public class ProductExcelUtility {

    public static List<Product> excelToProductList (InputStream inputStream, HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet("Product");

            if (sheet == null) {
                throw new AppException(ErrorCode.SHEET_NOT_FOUND);
            }

            Iterator<Row> rows = sheet.iterator();
            List<Product> valid = new ArrayList<>();
            List<Product> invalid = new ArrayList<>();

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
                Product product = new Product();

                while (cells.hasNext()) {
                    Cell currentCell = cells.next();
                    if (cellNumbers == 0) {
                        isValid &= isValidateId(currentCell);
                        product.setId(currentCell.getStringCellValue());
                    } else if (cellNumbers == 1) {
                        isValid &= isValidateName(currentCell);
                        product.setName(currentCell.getStringCellValue());
                    } else if (cellNumbers == 2) {
                        product.setPrice((float) currentCell.getNumericCellValue());
                        isValid &= (product.getPrice() > 0);
                    } else {
                        product.setStockQuantity((int) currentCell.getNumericCellValue());
                        isValid &= (product.getStockQuantity() >= 0);
                    }

                    ++ cellNumbers;
                }
                if (isValid) {
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

    public static boolean isValidateId (Cell cell) {
        if (!cell.getCellType().equals(CellType.STRING) || cell.getStringCellValue().length() != 10) {
            return false;
        }
        String s = cell.getStringCellValue().substring(0, 4);
        return s.equals("PRD-");
    }

    public static boolean isValidateName (Cell cell) {
        return  !(!cell.getCellType().equals(CellType.STRING) || cell.getStringCellValue().length() < 2 || cell.getStringCellValue().length() > 30);
    }

    public static void exportInvalidList (HttpServletResponse response, List<Product> list) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("InvalidProduct");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("NAME");
        header.createCell(2).setCellValue("PRICE");
        header.createCell(3).setCellValue("STOCK_QUANTITY");

        int rowNumbers = 1;
        for (Product product : list) {
            Row row = sheet.createRow(rowNumbers ++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getPrice());
            row.createCell(3).setCellValue(product.getStockQuantity());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=invalid_products.xlsx");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
