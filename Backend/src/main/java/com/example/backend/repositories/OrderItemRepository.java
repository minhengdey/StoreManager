package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
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
public class OrderItemRepository {

    DatabaseConfig databaseConfig;
    ProductRepository productRepository;
    OrdersRepository ordersRepository;

    public OrderItem addOrderItem (OrderItem orderItem) {
        String sql = "INSERT INTO STOREMANAGER.ORDER_ITEM (ID, QUANTITY, PRODUCT_ID, ORDERS_ID) VALUES (?, ?, ?, ?)";
        System.out.println(orderItem.getId());
        System.out.println(orderItem.getQuantity());
        System.out.println(orderItem.getProduct().getId());
        System.out.println(orderItem.getOrders().getId());

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, orderItem.getId());
            preparedStatement.setInt(2, orderItem.getQuantity());
            preparedStatement.setString(3, orderItem.getProduct().getId());
            preparedStatement.setString(4, orderItem.getOrders().getId());

            preparedStatement.executeUpdate();

            return orderItem;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsById (String id) {
        String sql = "SELECT 1 FROM STOREMANAGER.ORDER_ITEM WHERE ID = ? FETCH FIRST 1 ROWS ONLY";

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
                Orders orders = ordersRepository.findById(resultSet.getString("orders_id"));

                return new OrderItem(id, quantity, product, orders);
            } else {
                throw new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public OrderItem saveOrderItem (OrderItem orderItem) {
        String sql = "UPDATE STOREMANAGER.ORDER_ITEM SET QUANTITY = ?, PRODUCT_ID = ?, ORDERS_ID = ? WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderItem.getQuantity());
            preparedStatement.setString(2, orderItem.getProduct().getId());
            preparedStatement.setString(3, orderItem.getOrders().getId());
            preparedStatement.setString(4, orderItem.getId());

            preparedStatement.executeUpdate();

            return orderItem;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public void deleteOrderItem (String id) {
        String sql = "DELETE FROM STOREMANAGER.ORDER_ITEM WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public List<OrderItem> getAllByProductId (String productId, int page, int pageSize) {
        String sql = "SELECT * FROM STOREMANAGER.ORDER_ITEM WHERE PRODUCT_ID = ? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (page - 1) * pageSize;

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, productId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, pageSize);

            List<OrderItem> list = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                Integer quantity = resultSet.getInt("quantity");
                String ordersId = resultSet.getString("orders_id");

                list.add(new OrderItem(id, quantity, productRepository.findById(productId), ordersRepository.findById(ordersId)));
            }

            return list;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }
}
