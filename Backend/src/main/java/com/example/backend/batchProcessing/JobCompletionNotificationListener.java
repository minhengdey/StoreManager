package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductExcelSkipListener productExcelSkipListener;

    @Override
    // lắng nghe sự kiện sau khi xong job
    public void afterJob(JobExecution jobExecution) {
        log.info(jobExecution.getStatus().name());
        List<Product> invalidProducts = productExcelSkipListener.getInvalidProducts();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("InvalidProduct");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("NAME");
        header.createCell(2).setCellValue("PRICE");
        header.createCell(3).setCellValue("STOCK_QUANTITY");
        header.createCell(4).setCellValue("ERROR");

        int rowNumbers = 0;
        for (Product product : invalidProducts) {
            Row row = sheet.createRow(++ rowNumbers);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getPrice());
            row.createCell(3).setCellValue(product.getStockQuantity());
            row.createCell(4).setCellValue(product.getImportMessage());
//            System.out.println(product.getId());
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream out = new FileOutputStream("invalid_products.xlsx")) {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            List<Product> list = productRepository.getAllProduct();

            // log ra các product vừa được thêm vào DB
            for (Product product : list) {
                log.info("Found <{}> in the database.", product.getId());
            }

            log.info(String.valueOf(new Date()));
        }
    }
}
