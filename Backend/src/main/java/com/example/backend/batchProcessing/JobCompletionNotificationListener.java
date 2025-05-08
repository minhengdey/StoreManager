package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import com.example.backend.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    // lắng nghe sự kiện sau khi xong job
    public void afterJob(JobExecution jobExecution) {
        log.info(jobExecution.getStatus().name());
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
