package com.example.backend.services;

import com.example.backend.dto.request.TransactionRequest;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.enums.StatusOrder;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.TransactionMapper;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
import com.example.backend.models.Product;
import com.example.backend.models.Transaction;
import com.example.backend.repositories.OrdersRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.TransactionRepository;
import com.example.backend.utils.IdGenerator;
import com.example.backend.utils.PaymentSimulator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionService {

    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    OrdersRepository ordersRepository;
    ProductRepository productRepository;

    public TransactionResponse getById (String id) {
        return transactionMapper.toResponse(transactionRepository.getById(id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = AppException.class)
    public TransactionResponse processTransaction (String orderId, TransactionRequest request) throws SQLException {
        Orders orders = ordersRepository.findById(orderId);
        if (orders.getOrderItems().isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_ORDER_ITEM);
        }
        Transaction transaction = transactionMapper.toTransaction(request);
        String id = IdGenerator.generateId("TRS");
        while (transactionRepository.existsById(id)) {
            id = IdGenerator.generateId("TRS");
        }
        transaction.setId(id);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(StatusOrder.PENDING);
        transaction.setOrders(orders);

        boolean isSuccess = PaymentSimulator.mockPayment(request.getPaymentMethod());
        System.out.println(isSuccess);
        if (isSuccess) {
            try {
                productRepository.processTransaction(orders.getOrderItems());
                for (OrderItem orderItem : orders.getOrderItems()) {
                    orderItem.setProduct(productRepository.findById(orderItem.getProduct().getId()));
                }
            } catch (AppException e) {
                transaction.setStatus(StatusOrder.FAILED);
                transactionRepository.addTransaction(transaction);
                throw e;
            }
        } else {
            transaction.setStatus(StatusOrder.FAILED);
            transactionRepository.addTransaction(transaction);
            throw new AppException(ErrorCode.TRANSACTION_FAILED);
        }
        if (transaction.getStatus().equals(StatusOrder.PENDING)) {
            transaction.setStatus(StatusOrder.SUCCESS);
            ordersRepository.saveOrder(orders);
        }

        return transactionMapper.toResponse(transactionRepository.addTransaction(transaction));
    }
}
