package com.example.backend.models;

import com.example.backend.enums.PaymentMethod;
import com.example.backend.enums.StatusOrder;
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
    StatusOrder status;
    PaymentMethod paymentMethod;
}
