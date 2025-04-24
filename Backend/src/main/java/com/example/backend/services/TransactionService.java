package com.example.backend.services;

import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.enums.StatusOrder;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.TransactionMapper;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
import com.example.backend.models.Transaction;
import com.example.backend.repositories.OrdersRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.TransactionRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionService {

    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    OrdersRepository ordersRepository;
    ProductRepository productRepository;


    @Transactional
    public TransactionResponse addTransaction (TransactionRequest request, String orderId) {
        Orders orders = ordersRepository.findById(orderId);
        Transaction transaction = transactionMapper.toTransaction(request);
        String id = IdGenerator.generateId("TRS");
        while (transactionRepository.existsById(id)) {
            id = IdGenerator.generateId("TRS");
        }
        transaction.setId(id);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(StatusOrder.PENDING);
        transaction.setOrders(orders);

        return transactionMapper.toResponse(transactionRepository.addTransaction(transaction));
    }

    @Transactional
    public TransactionResponse updateTransaction (String id, TransactionRequest request, String orderId) {
        Orders orders = ordersRepository.findById(orderId);
        Transaction transaction = transactionRepository.getById(id);
        transactionMapper.update(transaction, request);
        if (transaction.getStatus().equals(StatusOrder.FAILED)) {
            transactionRepository.saveTransaction(transaction);

            throw new AppException(ErrorCode.TRANSACTION_FAILED);
        }
        if (request.getStatus().equals(StatusOrder.SUCCESS)) {
            for (OrderItem orderItem : orders.getOrderItems()) {
                orderItem.getProduct().setStockQuantity(orderItem.getProduct().getStockQuantity() - orderItem.getQuantity());
                productRepository.saveProduct(orderItem.getProduct());

            }
        }
        return transactionMapper.toResponse(transactionRepository.saveTransaction(transaction));
    }

    public TransactionResponse getById (String id) {
        return transactionMapper.toResponse(transactionRepository.getById(id));
    }
}
