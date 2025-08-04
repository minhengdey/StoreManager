package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.enums.PaymentMethod;
import com.example.backend.enums.StatusOrder;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Transaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionRepository {

    DatabaseConfig databaseConfig;
    OrdersRepository ordersRepository;

    public Transaction addTransaction (Transaction transaction) {
        String sql = "INSERT INTO STOREMANAGER.TRANSACTIONS (ID, ORDERS_ID, TRANSACTION_DATE, STATUS, PAYMENT_METHOD) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, transaction.getId());
            preparedStatement.setString(2, transaction.getOrders().getId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(transaction.getTransactionDate()));
            preparedStatement.setString(4, String.valueOf(transaction.getStatus()));
            preparedStatement.setString(5, String.valueOf(transaction.getPaymentMethod()));

            preparedStatement.executeUpdate();

            return transaction;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsById (String id) {
        String sql = "SELECT 1 FROM STOREMANAGER.TRANSACTIONS WHERE ID = ? FETCH FIRST 1 ROWS ONLY";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Transaction getById (String id) {
        String sql = "SELECT * FROM STOREMANAGER.TRANSACTIONS WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(id);
                transaction.setStatus(StatusOrder.valueOf(resultSet.getString("status")));
                transaction.setPaymentMethod(PaymentMethod.valueOf(resultSet.getString("payment_method")));
                transaction.setTransactionDate(resultSet.getTimestamp("transaction_date").toLocalDateTime());
                transaction.setOrders(ordersRepository.findById(resultSet.getString("orders_id")));

                return transaction;
            } else {
                throw new AppException(ErrorCode.TRANSACTION_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Transaction saveTransaction (Transaction transaction) {
        String sql = "UPDATE STOREMANAGER.TRANSACTIONS SET ORDERS_ID = ?, TRANSACTION_DATE = ?, STATUS = ?, PAYMENT_METHOD = ? WHERE ID = ?";

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, transaction.getOrders().getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(transaction.getTransactionDate()));
            preparedStatement.setString(3, String.valueOf(transaction.getStatus()));
            preparedStatement.setString(4, String.valueOf(transaction.getPaymentMethod()));
            preparedStatement.setString(5, transaction.getId());

            preparedStatement.executeUpdate();

            return transaction;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }
}
