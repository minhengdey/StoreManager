package com.example.backend.repository;

import com.example.backend.config.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exception.AppException;
import com.example.backend.model.Product;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductRepository {

    DatabaseConfig databaseConfig;

    public Product addProduct (Product product) {
        String sql = "INSERT INTO STOREMANAGER.PRODUCTS (ID, NAME, PRICE, STOCKQUANTITY) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, product.getId());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setFloat(3, product.getPrice());
            preparedStatement.setInt(4, product.getStockQuantity());

            preparedStatement.executeUpdate();

            return product;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsByName (String name) {
        String sql = "SELECT * FROM STOREMANAGER.PRODUCTS WHERE NAME = ?";
        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.PRODUCTS WHERE ID = ?";
        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }
}
