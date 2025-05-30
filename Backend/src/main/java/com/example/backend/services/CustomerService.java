package com.example.backend.services;

import com.example.backend.dto.request.CustomerRequest;
import com.example.backend.dto.response.CustomerResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.CustomerMapper;
import com.example.backend.models.Customer;
import com.example.backend.repositories.CustomerRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    public CustomerResponse addCustomer (CustomerRequest request) {
        Customer customer = customerMapper.toCustomer(request);

        String id = IdGenerator.generateId("CTM");
        while (customerRepository.existsById(id)) {
            id = IdGenerator.generateId("CTM");
        }
        customer.setId(id);

        return customerMapper.toResponse(customerRepository.addCustomer(customer));
    }

    public CustomerResponse getCustomerById (String id) {
        return customerMapper.toResponse(customerRepository.findById(id));
    }

    public CustomerResponse updateCustomer (CustomerRequest request, String id) {
        Customer customer = customerRepository.findById(id);
        customerMapper.update(customer, request);
        return customerMapper.toResponse(customerRepository.saveCustomer(customer));
    }

    public void deleteCustomer (String id) {
        customerRepository.deleteCustomer(id);
    }

    public List<CustomerResponse> getAllCustomer (int page, int pageSize) {
        return customerRepository.getAllCustomer(page, pageSize).stream().map(customerMapper::toResponse).toList();
    }
}
