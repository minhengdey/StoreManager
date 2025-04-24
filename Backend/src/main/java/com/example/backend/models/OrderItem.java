package com.example.backend.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    String id;
    Integer quantity;
    Product product;
    @JsonIgnore
    Orders orders;
}
