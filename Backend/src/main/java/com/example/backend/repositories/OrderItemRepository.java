package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Product;
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
public class OrderItemRepository {

    DatabaseConfig databaseConfig;
    ProductRepository productRepository;

    public OrderItem addOrderItem (OrderItem orderItem) {
        String sql = "INSERT INTO STOREMANAGER.ORDER_ITEM (ID, QUANTITY, PRODUCT_ID) VALUES (?, ?, ?)";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, orderItem.getId());
            preparedStatement.setInt(2, orderItem.getQuantity());
            preparedStatement.setString(3, orderItem.getProduct().getId());

            preparedStatement.executeUpdate();

            return orderItem;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.ORDER_ITEM WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public OrderItem findById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.ORDER_ITEM WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Integer quantity = resultSet.getInt("quantity");
                Product product = productRepository.findById(resultSet.getString("product_id"));

                return new OrderItem(id, quantity, product);
            } else {
                throw new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }    }
}
