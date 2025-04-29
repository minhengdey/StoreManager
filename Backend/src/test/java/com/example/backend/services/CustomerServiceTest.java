package com.example.backend.services;

import com.example.backend.dto.request.CustomerRequest;
import com.example.backend.dto.response.CustomerResponse;
import com.example.backend.mappers.CustomerMapper;
import com.example.backend.models.Customer;
import com.example.backend.repositories.CustomerRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerServiceTest {
    @Mock
    CustomerRepository customerRepository;
    @Mock
    CustomerMapper customerMapper;
    @InjectMocks
    CustomerService customerService;

    @Test
    void addCustomer_ShouldReturnResponse () {
        CustomerRequest request = new CustomerRequest("John Doe", "123456789", "john.doe@example.com");

        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setPhone("123456789");
        customer.setEmail("john.doe@example.com");

        when(customerMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);

        try (MockedStatic<IdGenerator> mockIdGen = Mockito.mockStatic(IdGenerator.class)) {
            mockIdGen.when(() -> IdGenerator.generateId("CTM"))
                    .thenReturn("CTM-123AAA");

            when(customerRepository.existsById("CTM-123AAA")).thenReturn(false);

            customer.setId("CTM-123AAA");

            when(customerRepository.addCustomer(customer)).thenReturn(customer);

            CustomerResponse expected = new CustomerResponse("CTM-123AAA", request.getName(), request.getPhone(), request.getEmail());
            when(customerMapper.toResponse(customer)).thenReturn(expected);

            CustomerResponse actual = customerService.addCustomer(request);

            assertEquals(expected, actual);
            verify(customerRepository).addCustomer(customer);
        }
    }

    @Test
    void getCustomerById_ShouldReturnResponse () {
        String id = "CTM-123AAA";
        Customer customer = new Customer("CTM-123AAA", "John Doe", "123456789", "john.doe@example.com");

        when(customerRepository.findById(id)).thenReturn(customer);

        CustomerResponse expected = new CustomerResponse("CTM-123AAA", "John Doe", "123456789", "john.doe@example.com");
        when(customerMapper.toResponse(customer)).thenReturn(expected);

        CustomerResponse actual = customerService.getCustomerById(id);

        assertEquals(expected, actual);
        verify(customerRepository).findById(id);
    }

    @Test
    void updateCustomer_ShouldUpdateAndReturnResponse () {
        String id = "CTM-123AAA";
        CustomerRequest request = new CustomerRequest("John Doe", "123456789", "john.doe@example.com");
        Customer customer = new Customer();

        when(customerRepository.findById(id)).thenReturn(customer);

        customer.setId(id);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());

        doNothing().when(customerMapper).update(customer, request);

        when(customerRepository.saveCustomer(customer)).thenReturn(customer);

        CustomerResponse expected = new CustomerResponse("CTM-123AAA", "John Doe", "123456789", "john.doe@example.com");
        when(customerMapper.toResponse(customer)).thenReturn(expected);

        CustomerResponse actual = customerService.updateCustomer(request, id);

        assertEquals(expected, actual);
        verify(customerRepository).saveCustomer(customer);
    }

    @Test
    void deleteCustomer_ShouldCallRepository () {
        String id = "CTM-123AAA";
        doNothing().when(customerRepository).deleteCustomer(id);

        customerService.deleteCustomer(id);

        verify(customerRepository).deleteCustomer(id);
    }

    @Test
    void getAllCustomer_ShouldReturnList () {
        int page = 1;
        int pageSize = 10;

        Customer customer1 = new Customer("CTM-123AAA", "John Doe", "123456789", "john.doe@example.com");
        Customer customer2 = new Customer("CTM-123TTT", "Minh Anh", "0918927204", "dlminhanh272@gmail.com");

        when(customerRepository.getAllCustomer(page, pageSize)).thenReturn(List.of(customer1, customer2));

        CustomerResponse response1 = new CustomerResponse("CTM-123AAA", "John Doe", "123456789", "john.doe@example.com");
        CustomerResponse response2 = new CustomerResponse("CTM-123TTT", "Minh Anh", "0918927204", "dlminhanh272@gmail.com");
        List<CustomerResponse> expected = List.of(response1, response2);

        when(customerMapper.toResponse(customer1)).thenReturn(response1);
        when(customerMapper.toResponse(customer2)).thenReturn(response2);

        List<CustomerResponse> actual = customerService.getAllCustomer(page, pageSize);

        assertEquals(expected, actual);
    }

}
