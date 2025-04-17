package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.OrderItem;
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

    public OrderItem addOrderItem (OrderItem orderItem) {
        String sql = "INSERT INTO STOREMANAGER.ORDER_ITEM (ID, QUANTITY, PRODUCT_ID) VALUES (?, ?, ?)";
        System.out.println(orderItem.getId());
        System.out.println(orderItem.getQuantity());
        System.out.println(orderItem.getProduct().getName());

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
}
