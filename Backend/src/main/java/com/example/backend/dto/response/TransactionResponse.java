package com.example.backend.dto.response;

import com.example.backend.enums.PaymentMethod;
import com.example.backend.enums.StatusOrder;
import com.example.backend.models.Orders;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {
    String id;
    Orders orders;
    LocalDateTime transactionDate;
    StatusOrder status;
    PaymentMethod paymentMethod;
}
