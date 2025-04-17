package com.example.backend.dto.response;

import com.example.backend.models.Customer;
import com.example.backend.models.OrderItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrdersResponse {
    String id;
    LocalDateTime orderDate;
    Float totalAmount;
    Customer customer;
    List<OrderItem> orderItems;
}
