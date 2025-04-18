package com.example.backend.mappers;

import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.models.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toTransaction (TransactionRequest request);
    TransactionResponse toResponse (Transaction transaction);
}
