package com.example.backend.dto.request;

import com.example.backend.enums.PaymentMethod;
import com.example.backend.enums.StatusOrder;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionRequest {
    PaymentMethod paymentMethod;
}
