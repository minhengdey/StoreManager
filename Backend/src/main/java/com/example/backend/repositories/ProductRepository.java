package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.dto.request.ProductRequest;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Product;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductRepository {

    DatabaseConfig databaseConfig;

    public Product addProduct (Product product) {
        String sql = "INSERT INTO STOREMANAGER.PRODUCTS (ID, NAME, PRICE, STOCK_QUANTITY) VALUES (?, ?, ?, ?)";

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

    public Product findById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.PRODUCTS WHERE ID = ?";
        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Float price = resultSet.getFloat("price");
                Integer stockQuantity = resultSet.getInt("stock_quantity");

                return new Product(id, name, price, stockQuantity);
            } else {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Product findByName (String name) {
        String sql = "SELECT * FROM STOREMANAGER.PRODUCTS WHERE NAME = ?";
        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                Float price = resultSet.getFloat("price");
                Integer stockQuantity = resultSet.getInt("stock_quantity");

                return new Product(id, name, price, stockQuantity);
            } else {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Product saveProduct (Product product) {
        String sql = "UPDATE STOREMANAGER.PRODUCTS SET NAME = ?, PRICE = ?, STOCK_QUANTITY = ? WHERE ID = ?";
        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setFloat(2, product.getPrice());
            preparedStatement.setInt(3, product.getStockQuantity());
            preparedStatement.setString(4, product.getId());

            preparedStatement.executeUpdate();

            return product;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public void deleteProduct (String id) {
        String sql = "DELETE FROM STOREMANAGER.PRODUCTS WHERE ID = ?";
        try (Connection connection = databaseConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public List<Product> getAllProduct () {
        String sql = "SELECT * FROM STOREMANAGER.PRODUCTS";
        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Product> list = new ArrayList<>();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                Float price = resultSet.getFloat("price");
                Integer stockQuantity = resultSet.getInt("stock_quantity");

                list.add(new Product(id, name, price, stockQuantity));
            }

            return list;
        } catch (SQLException e) {
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
