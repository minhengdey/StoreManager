package com.example.backend.batchProcessing;

import com.example.backend.models.Product;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Configuration
public class BatchConfiguration {
    @Bean
    public FlatFileItemReader<Product> reader () {
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader")
                .resource(new ClassPathResource("product_100.csv"))
                .delimited()
                .names("id", "name", "price", "stockQuantity")
                .targetType(Product.class)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Product> invalidItemWriter () {
        return new FlatFileItemWriterBuilder<Product>()
                .name("invalid_products.csv")
                .encoding("UTF-8")
                .lineAggregator(new DelimitedLineAggregator<>() {{
                    setDelimiter(",");
                    setFieldExtractor(product -> new Object[] {
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getStockQuantity(),
                            "INVALID"
                    });
                }})
                .headerCallback(w -> w.write("id,name,price,stockQuantity"))
                .append(true)
                .build();
    }

    @Bean
    public SkipListener<Product, Product> skipListener (FlatFileItemWriter<Product> invalidItemWriter) {
        return new SkipListener<>() {
            final Path invalidFilePath = Paths.get("invalid_products.csv");

            @Override
            public void onSkipInProcess(Product item, Throwable t) {
                try (BufferedWriter writer = Files.newBufferedWriter(invalidFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    writer.write(String.format("%s,%s,%s,%s,INVALID\n",
                            item.getId(),
                            item.getName(),
                            item.getPrice(),
                            item.getStockQuantity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSkipInWrite(Product item, Throwable t) {
                try {
                    onSkipInProcess(item, t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSkipInRead(Throwable t) {
                SkipListener.super.onSkipInRead(t);
            }
        };
    }

    @Bean
    public ProductItemProcessor processor () {
        return new ProductItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Product> writer (DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .sql("INSERT INTO STOREMANAGER.PRODUCTS (ID, NAME, PRICE, STOCK_QUANTITY) VALUES (:id, :name, :price, :stockQuantity)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob (JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1 (JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                       FlatFileItemReader<Product> reader, ProductItemProcessor processor, JdbcBatchItemWriter<Product> writer,
                       SkipListener<Product, Product> skipListener) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .listener(skipListener)
                .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // Số thread tối thiểu luôn giữ
        executor.setMaxPoolSize(10);          // Số thread tối đa
        executor.setQueueCapacity(25);        // Hàng đợi task khi các thread đang bận
        executor.setThreadNamePrefix("batch_");
        executor.initialize();
        return executor;
    }
// cho chạy ngay khi chạy chương trình

//    @Bean
//    public CommandLineRunner run(JobLauncher jobLauncher, Job importUserJob) {
//        return args -> {
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//            jobLauncher.run(importUserJob, jobParameters);
//        };
//    }


}
