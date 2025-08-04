package com.example.backend.dto.request;

import com.example.backend.utils.IdGenerator;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String name;
    Float price;
    Integer stockQuantity;
}
