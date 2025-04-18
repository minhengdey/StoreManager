package com.example.backend.controllers;

import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.services.TransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {

    TransactionService transactionService;

    @PostMapping(value = "/{orderId}")
    public ApiResponse<TransactionResponse> addTransaction (@Valid @RequestBody TransactionRequest request,
                                                            @PathVariable("orderId") String orderId) {
        return ApiResponse.<TransactionResponse>builder()
                .code(1000)
                .result(transactionService.addTransaction(request, orderId))
                .build();
    }
}
