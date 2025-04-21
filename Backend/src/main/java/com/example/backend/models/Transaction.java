package com.example.backend.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    String id;
    Orders orders;
    LocalDateTime transactionDate;
    String status;
    String paymentMethod;
}
