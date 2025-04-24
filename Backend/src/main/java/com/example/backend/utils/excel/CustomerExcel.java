package com.example.backend.utils.excel;

import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Customer;
import com.example.backend.repositories.CustomerRepository;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerExcel {

    Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    Pattern PHONE_REGEX = Pattern.compile("^\\d{10}$");
    ArrayList<boolean[]> marks = new ArrayList<>();

    CustomerRepository customerRepository;

    public List<Customer> excelToCustomerList (InputStream inputStream, HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet("Customer");

            if (sheet == null) {
                throw new AppException(ErrorCode.SHEET_NOT_FOUND);
            }

            Iterator<Row> rows = sheet.iterator();

            List<Customer> valid = new ArrayList<>();
            List<Customer> invalid = new ArrayList<>();

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
                boolean isValid = true;
                boolean[] markCell = new boolean[4];

                while (cells.hasNext()) {
                    Cell currentCell = cells.next();
                    if (cellNumbers == 0) {
                        markCell[cellNumbers] = isValidId(currentCell);
                        customer.setId(currentCell.getStringCellValue());
                    } else if (cellNumbers == 1) {
                        markCell[cellNumbers] = isValidName(currentCell);
                        customer.setName(currentCell.getStringCellValue());
                    } else if (cellNumbers == 2) {
                        markCell[cellNumbers] = isValidPhone(currentCell);
                        customer.setPhone(currentCell.getStringCellValue());
                    } else {
                        markCell[cellNumbers] = isValidEmail(currentCell);
                        customer.setEmail(currentCell.getStringCellValue());
                    }
                    isValid &= markCell[cellNumbers];
                    ++ cellNumbers;
                }

                marks.add(markCell);

                if (isValid) {
                    valid.add(customer);
                } else {
                    invalid.add(customer);
                }
            }
            workbook.close();
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
        return s.equals("CTM-") && !customerRepository.existsById(cell.getStringCellValue());
    }

    public boolean isValidName (Cell cell) {
        return  !(!cell.getCellType().equals(CellType.STRING) || cell.getStringCellValue().length() < 2 || cell.getStringCellValue().length() > 30);
    }

    public boolean isValidPhone (Cell cell) {
        return (cell.getCellType().equals(CellType.STRING) && PHONE_REGEX.matcher(cell.getStringCellValue()).matches());
    }

    public boolean isValidEmail (Cell cell) {
        return (cell.getCellType().equals(CellType.STRING) && EMAIL_REGEX.matcher(cell.getStringCellValue()).matches());
    }

    public void exportInvalidList (HttpServletResponse response, List<Customer> list) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("InvalidCustomer");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("NAME");
        header.createCell(2).setCellValue("PHONE");
        header.createCell(3).setCellValue("EMAIL");
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowNumbers = 0;
        for (Customer customer : list) {
            Row row = sheet.createRow(rowNumbers + 1);
            row.createCell(0).setCellValue(customer.getId());
            row.createCell(1).setCellValue(customer.getName());
            row.createCell(2).setCellValue(customer.getPhone());
            row.createCell(3).setCellValue(customer.getEmail());
            for (int i = 0; i < 4; ++ i) {
                if (!marks.get(rowNumbers)[i]) {
                    row.getCell(i).setCellStyle(cellStyle);
                }
            }
            ++ rowNumbers;
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=invalid_customers.xlsx");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
