package com.example.backend.models;

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
    Float totalAmount;
    Customer customer;
    List<OrderItem> orderItems;
}
