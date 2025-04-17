package com.example.backend.mappers;

import com.example.backend.dto.request.CustomerRequest;
import com.example.backend.dto.response.CustomerResponse;
import com.example.backend.models.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer (CustomerRequest request);
    CustomerResponse toResponse (Customer customer);
}
