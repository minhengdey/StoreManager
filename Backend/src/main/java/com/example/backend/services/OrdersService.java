package com.example.backend.services;

import com.example.backend.dto.response.OrdersResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.OrdersMapper;
import com.example.backend.models.Customer;
import com.example.backend.models.Orders;
import com.example.backend.repositories.CustomerRepository;
import com.example.backend.repositories.OrdersRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersService {
    OrdersRepository ordersRepository;
    OrdersMapper ordersMapper;
    CustomerRepository customerRepository;

    public OrdersResponse createOrders (String customerId) {
        Customer customer = customerRepository.findById(customerId);
        Orders orders = new Orders();
        String id = IdGenerator.generateId("ORD");
        while (ordersRepository.existsById(id)) {
            id = IdGenerator.generateId("ORD");
        }
        orders.setId(id);
        orders.setCustomer(customer);
        orders.setOrderDate(LocalDateTime.now());
        orders.setTotalAmount(0F);

        return ordersMapper.toResponse(ordersRepository.addOrders(orders));
    }

    public OrdersResponse getById (String id) {
        return ordersMapper.toResponse(ordersRepository.findById(id));
    }

    public void deleteOrders (String id) {
        if (!ordersRepository.existsById(id)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        ordersRepository.deleteOrder(id);
    }

    public List<OrdersResponse> getAllOrders (int page, int pageSize) {
        return ordersRepository.getAllOrders(page, pageSize).stream().map(ordersMapper::toResponse).toList();
    }
}
