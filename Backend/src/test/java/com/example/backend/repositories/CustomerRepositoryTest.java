package com.example.backend.repositories;

import com.example.backend.configs.DatabaseConfig;
import com.example.backend.models.Customer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRepositoryTest {
    @Mock
    DatabaseConfig databaseConfig;
    @Mock
    Connection connection;
    @Mock
    PreparedStatement preparedStatement;
    @Mock
    ResultSet resultSet;
    @InjectMocks
    CustomerRepository customerRepository;
    Customer customer;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        customer = new Customer("CTM-123aaa", "John Doe", "123456789", "john.doe@example.com");

        when(databaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testAddCustomer() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Customer result = customerRepository.addCustomer(customer);

        assertNotNull(result);
        assertEquals("CTM-123aaa", result.getId());
        verify(preparedStatement, times(1)).executeUpdate();
    }
}
