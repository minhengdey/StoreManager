package com.example.backend.services;

import com.example.backend.dto.request.OrderItemRequest;
import com.example.backend.dto.response.OrderItemResponse;
import com.example.backend.enums.ErrorCode;
import com.example.backend.exceptions.AppException;
import com.example.backend.mappers.OrderItemMapper;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Orders;
import com.example.backend.models.Product;
import com.example.backend.repositories.OrderItemRepository;
import com.example.backend.repositories.OrdersRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.utils.IdGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {

    OrderItemRepository orderItemRepository;
    OrderItemMapper orderItemMapper;
    ProductRepository productRepository;
    OrdersRepository ordersRepository;

    public OrderItemResponse addOrderItem (OrderItemRequest request, String productId, String ordersId) {
        Product product = productRepository.findById(productId);
        Orders orders = ordersRepository.findById(ordersId);
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new AppException(ErrorCode.ORDER_ITEM_INVALID);
        }
        OrderItem orderItem = orderItemMapper.toOrderItem(request);
        orderItem.setProduct(product);

        String id = IdGenerator.generateId("ORI");
        while (orderItemRepository.existsById(id)) {
            id = IdGenerator.generateId("ORI");
        }
        orderItem.setId(id);
        orders.setTotalAmount(orders.getTotalAmount() + orderItem.getQuantity() * orderItem.getProduct().getPrice());
        orders.getOrderItems().add(orderItem);
        orderItem.setOrders(orders);
        ordersRepository.saveOrder(orders);

        return orderItemMapper.toResponse(orderItemRepository.addOrderItem(orderItem));
    }

    public OrderItemResponse getById (String id) {
        return orderItemMapper.toResponse(orderItemRepository.findById(id));
    }

    public OrderItemResponse updateOrderItem (OrderItemRequest request, String id) {
        OrderItem orderItem = orderItemRepository.findById(id);
        if (request.getQuantity() > orderItem.getProduct().getStockQuantity()) {
            throw new AppException(ErrorCode.ORDER_ITEM_INVALID);
        }
        orderItemMapper.update(orderItem, request);
        return orderItemMapper.toResponse(orderItemRepository.saveOrderItem(orderItem));
    }

    public void deleteOrderItem (String id) {
        OrderItem orderItem = orderItemRepository.findById(id);
        Orders orders = orderItem.getOrders();
        orders.setTotalAmount(orders.getTotalAmount() - orderItem.getQuantity() * orderItem.getProduct().getPrice());
        orders.getOrderItems().remove(orderItem);
        ordersRepository.saveOrder(orders);
        orderItemRepository.deleteOrderItem(id);
    }

    public List<OrderItemResponse> getAllByProductId (String productId, int page, int pageSize) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return orderItemRepository.getAllByProductId(productId, page, pageSize).stream().map(orderItemMapper::toResponse).toList();
    }
}
