package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Customer;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
import com.example.backend.models.Product;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersRepository {

    DatabaseConfig databaseConfig;
    CustomerRepository customerRepository;
    ProductRepository productRepository;

    public Orders addOrders (Orders orders) {
        String sql = "INSERT INTO STOREMANAGER.ORDERS (ID, ORDER_DATE, TOTAL_AMOUNT, CUSTOMER_ID) VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, orders.getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(orders.getOrderDate()));
            preparedStatement.setFloat(3, orders.getTotalAmount());
            preparedStatement.setString(4, orders.getCustomer().getId());

            preparedStatement.executeUpdate();

            return orders;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Orders findById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.ORDERS WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                LocalDateTime orderDate = resultSet.getTimestamp("order_date").toLocalDateTime();
                Float totalAmount = resultSet.getFloat("total_amount");
                Customer customer = customerRepository.findById(resultSet.getString("customer_id"));

                String sql1 = "SELECT * FROM STOREMANAGER.ORDER_ITEM WHERE ORDERS_ID = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);

                preparedStatement1.setString(1, id);

                ResultSet resultSet1 = preparedStatement1.executeQuery();
                List<OrderItem> list = new ArrayList<>();

                while (resultSet1.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(resultSet1.getString("id"));
                    orderItem.setQuantity(resultSet1.getInt("quantity"));
                    orderItem.setProduct(productRepository.findById(resultSet1.getString("product_id")));

                    list.add(orderItem);
                }

                return new Orders(id, orderDate, totalAmount, customer, list);
            } else {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Orders saveOrder (Orders orders) {
        String sql = "UPDATE STOREMANAGER.ORDERS SET ORDER_DATE = ?, TOTAL_AMOUNT = ?, CUSTOMER_ID = ? WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(orders.getOrderDate()));
            preparedStatement.setFloat(2, orders.getTotalAmount());
            preparedStatement.setString(3, orders.getCustomer().getId());
            preparedStatement.setString(4, orders.getId());

            preparedStatement.executeUpdate();

            return orders;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public List<Orders> getAllOrders () {
        String sql = "SELECT * FROM STOREMANAGER.ORDERS";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Orders> list = new ArrayList<>();

            while (resultSet.next()) {
                LocalDateTime orderDate = resultSet.getTimestamp("order_date").toLocalDateTime();
                Float totalAmount = resultSet.getFloat("total_amount");
                Customer customer = customerRepository.findById(resultSet.getString("customer_id"));

                String sql1 = "SELECT * FROM STOREMANAGER.ORDER_ITEM WHERE ORDERS_ID = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);

                preparedStatement1.setString(1, resultSet.getString("id"));

                ResultSet resultSet1 = preparedStatement1.executeQuery();
                List<OrderItem> list1 = new ArrayList<>();

                while (resultSet1.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(resultSet1.getString("id"));
                    orderItem.setQuantity(resultSet1.getInt("quantity"));
                    orderItem.setProduct(productRepository.findById(resultSet1.getString("product_id")));

                    list1.add(orderItem);
                }

                list.add(new Orders(resultSet.getString("id"), orderDate, totalAmount, customer, list1));
            }

            return list;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public void deleteOrder (String id) {
        String sql = "DELETE FROM STOREMANAGER.ORDERS WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.ORDERS WHERE ID = ?";

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
