package com.example.backend.config;

import com.example.backend.enums.ErrorCode;
import com.example.backend.exception.AppException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseConfig {
    @Value(value = "${spring.datasource.url}")
    String DB_URL;

    @Value(value = "${spring.datasource.username}")
    String DB_USERNAME;

    @Value(value = "${spring.datasource.password}")
    String DB_PASSWORD;

    @Value(value = "${spring.datasource.driver-class-name}")
    String DB_DRIVER_CLASS_NAME;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName(DB_DRIVER_CLASS_NAME);
        return new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource().getConnection();
    }
}
