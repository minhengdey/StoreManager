package com.example.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {

    CLASS_NOT_FOUND(1001, "Cannot found database driver class name", HttpStatus.NOT_FOUND),
    CONNECT_ERROR(1002, "Cannot connect database", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(1003, "Product existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1004, "Product not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode statusCode;
}
