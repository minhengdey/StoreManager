package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import jakarta.validation.ValidationException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLIntegrityConstraintViolationException;

@Configuration
public class BatchConfiguration {
    @Bean
    public FlatFileItemReader<Product> reader () { // đọc file csv đầu vào
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader")
                .resource(new ClassPathResource("product_100.csv"))
                .delimited() // dữ liệu phân cách bằng dấu phẩy
                .names("id", "name", "price", "stockQuantity")
                .targetType(Product.class)
                .linesToSkip(1) // skip 1 dòng header
                .build();
    }

    @Bean
    // viết các product invalid vào file csv (tạm thời không dùng vì phải quản lý open, close ExecutionContext)
    public FlatFileItemWriter<Product> invalidItemWriter () {
        return new FlatFileItemWriterBuilder<Product>()
                .name("invalid_products.csv")
                .encoding("UTF-8")
                .lineAggregator(new DelimitedLineAggregator<>() {{
                    setDelimiter(","); // đặt dấu phẩy ngăn cách các cột dữ liệu
                    setFieldExtractor(product -> new Object[] { // các cột dữ liệu
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getStockQuantity(),
                            "INVALID"
                    });
                }})
                .headerCallback(w -> w.write("id,name,price,stockQuantity")) // viết header
                .append(true) // cho phép append
                .build();
    }

    @Bean
    // kiểm soát dữ liệu đầu vào
    public ProductItemProcessor processor () {
        return new ProductItemProcessor();
    }

    @Bean
    // insert các dữ liệu hợp lệ vào DB
    public JdbcBatchItemWriter<Product> writer (DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .sql("INSERT INTO STOREMANAGER.PRODUCTS (ID, NAME, PRICE, STOCK_QUANTITY) VALUES (:id, :name, :price, :stockQuantity)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    // khởi tạo job batch
    public Job importProductJob (JobRepository jobRepository, Step step1, Step step2, JobCompletionNotificationListener listener) {
        return new JobBuilder("importProduct", jobRepository) // tạo job tên là importProductJob
                .listener(listener) // lắng nghe các sự kiện của job (before, after,...)
                .start(step2) // bắt đầu với step1
//                .next(step2) // tiếp theo với step2
                .build();
    }

    @Bean
    // khởi tạo step1
    public Step step1 (JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                       FlatFileItemReader<Product> reader, ProductItemProcessor processor, JdbcBatchItemWriter<Product> writer,
                       ProductCsvSkipListener productCsvSkipListener) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(1000, transactionManager) // xử lý theo từng chunk (1000), xong 1 chunk thì commit lên 1 lần rồi tiếp tục xử lý chunk tiếp theo
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor()) // đa luồng
                .faultTolerant() // cho phép skip khi có lỗi
                .skip(ValidationException.class) // sẽ skip các dữ liệu bị catch Exception
                .skip(DuplicateKeyException.class)
                .skip(SQLIntegrityConstraintViolationException.class)
                .skipLimit(1000) // số lượng skip tối đa
                .listener(productCsvSkipListener)
                .build();
    }

    @Bean
    public Step step2 (JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                       ItemReader<Product> excelItemReader, ProductItemProcessor processor, JdbcBatchItemWriter<Product> writer,
                       ProductExcelSkipListener productExcelSkipListener) {
        return new StepBuilder("step2", jobRepository)
                .<Product, Product>chunk(50, transactionManager) // xử lý theo từng chunk (1000), xong 1 chunk thì commit lên 1 lần rồi tiếp tục xử lý chunk tiếp theo
                .reader(excelItemReader)
                .processor(processor)
                .writer(writer)
//                .taskExecutor(taskExecutor()) // đa luồng
                .faultTolerant() // cho phép skip khi có lỗi
                .skip(ValidationException.class) // sẽ skip các dữ liệu bị catch Exception
                .skip(DuplicateKeyException.class)
                .skip(SQLIntegrityConstraintViolationException.class)
                .skipLimit(10000) // số lượng skip tối đa
                .listener(productExcelSkipListener)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Product> excelItemReader(@Value("#{jobParameters['filePath']}") String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            throw new IllegalArgumentException("File không tồn tại hoặc rỗng: " + filePath);
        }

        InputStream inputStream = new FileInputStream(file);
        return new ExcelItemReader(inputStream);
    }


    @Bean
    public TaskExecutor taskExecutor() {
        // dùng thread pool tái sử dụng luồng -> đỡ tốn tài nguyên
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // Số thread tối thiểu luôn giữ
        executor.setMaxPoolSize(10);          // Số thread tối đa
        executor.setQueueCapacity(25);        // Hàng đợi task khi các thread đang bận
        executor.setThreadNamePrefix("batch_");
        executor.initialize();
        return executor;
    }

    /*
    import 1 triệu dòng dữ liệu:
    - spring batch mất ~40s
    - spring batch + multithreading mất ~10s
     */

// cho chạy ngay khi chạy chương trình
//    @Bean
//    public CommandLineRunner run(JobLauncher jobLauncher, Job importProductJob) {
//        return args -> {
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//            jobLauncher.run(importProductJob, jobParameters);
//        };
//    }


}
