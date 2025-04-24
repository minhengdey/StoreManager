package com.example.backend.mappers;

import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.models.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {
    Transaction toTransaction (TransactionRequest request);
    TransactionResponse toResponse (Transaction transaction);
    void update (@MappingTarget Transaction transaction, TransactionRequest request);
}
