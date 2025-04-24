package com.example.backend.dto.response;

import com.example.backend.models.Orders;
import com.example.backend.models.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    String id;
    Integer quantity;
    Product product;
    Orders orders;
}
