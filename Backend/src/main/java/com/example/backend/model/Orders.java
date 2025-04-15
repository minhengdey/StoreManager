package com.example.backend.model;

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
public class Orders {
    String id;
    LocalDateTime orderDate;
    Double totalAmount;
    Customer customer;
    List<OrderItem> orderItems;
}
