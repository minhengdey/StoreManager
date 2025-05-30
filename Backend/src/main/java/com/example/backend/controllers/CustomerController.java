package com.example.backend.controllers;

import com.example.backend.dto.request.CustomerRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.CustomerResponse;
import com.example.backend.services.CustomerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    CustomerService customerService;

    @PostMapping()
    public ApiResponse<CustomerResponse> addCustomer (@Valid @RequestBody CustomerRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .code(1000)
                .result(customerService.addCustomer(request))
                .build();
    }

    @GetMapping(value = "/{id}")
    public ApiResponse<CustomerResponse> getCustomerById (@PathVariable("id") String id) {
        return ApiResponse.<CustomerResponse>builder()
                .code(1000)
                .result(customerService.getCustomerById(id))
                .build();
    }

    @PutMapping(value = "/{id}")
    public ApiResponse<CustomerResponse> updateCustomer (@Valid @RequestBody CustomerRequest request, @PathVariable("id") String id) {
        return ApiResponse.<CustomerResponse>builder()
                .code(1000)
                .result(customerService.updateCustomer(request, id))
                .build();
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCustomer (@PathVariable("id") String id) {
        customerService.deleteCustomer(id);
    }

    @GetMapping(value = "/all")
    public ApiResponse<List<CustomerResponse>> getAllCustomer (@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        return ApiResponse.<List<CustomerResponse>>builder()
                .code(1000)
                .result(customerService.getAllCustomer(page, pageSize))
                .build();
    }
}
