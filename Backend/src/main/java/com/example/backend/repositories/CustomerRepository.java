package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.models.Customer;
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
public class CustomerRepository {

    DatabaseConfig databaseConfig;

    public Customer addCustomer (Customer customer) {
        StringBuilder sql = new StringBuilder("INSERT INTO STOREMANAGER.CUSTOMER (ID, NAME, PHONE, EMAIL) VALUES (?, ?, ?, ?)");

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

            preparedStatement.setString(1, customer.getId());
            preparedStatement.setString(2, customer.getName());
            preparedStatement.setString(3, customer.getPhone());
            preparedStatement.setString(4, customer.getEmail());

            preparedStatement.executeUpdate();

            return customer;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public boolean existsById (String id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM STOREMANAGER.CUSTOMER WHERE ID = ?");

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Customer findById (String id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM STOREMANAGER.CUSTOMER WHERE ID = ?");

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");

                return new Customer(id, name, phone, email);
            } else {
                throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public Customer saveCustomer (Customer customer) {
        StringBuilder sql = new StringBuilder("UPDATE STOREMANAGER.CUSTOMER SET NAME = ?, PHONE = ?, EMAIL = ? WHERE ID = ?");

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getPhone());
            preparedStatement.setString(3, customer.getEmail());
            preparedStatement.setString(4, customer.getId());

            preparedStatement.executeUpdate();

            return customer;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public void deleteCustomer (String id) {
        StringBuilder sql = new StringBuilder("DELETE FROM STOREMANAGER.CUSTOMER WHERE ID = ?");

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }

    public List<Customer> getAllCustomer (int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM STOREMANAGER.CUSTOMER OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        int offset = (page - 1) * pageSize;

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            preparedStatement.setInt(1, offset);
            preparedStatement.setInt(2, pageSize);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Customer> list = new ArrayList<>();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");

                list.add(new Customer(id, name, phone, email));
            }

            return list;
        } catch (SQLException e) {
            throw new AppException(ErrorCode.CONNECT_ERROR);
        }
    }
}
