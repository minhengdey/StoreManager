package com.example.backend.controllers;

import com.example.backend.dto.request.CustomerRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.CustomerResponse;
import com.example.backend.services.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    CustomerService customerService;

    @PostMapping()
    public ApiResponse<CustomerResponse> addCustomer (@RequestBody CustomerRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .code(1000)
                .result(customerService.addCustomer(request))
                .build();
    }
}
