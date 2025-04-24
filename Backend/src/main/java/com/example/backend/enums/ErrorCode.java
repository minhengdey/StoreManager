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
    UNCATEGORIZED_EXCEPTION(1003, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_EXISTED(1004, "Product existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1005, "Product not found", HttpStatus.NOT_FOUND),
    CUSTOMER_NOT_FOUND(1006, "Customer not found", HttpStatus.NOT_FOUND),
    ORDER_ITEM_NOT_FOUND(1007, "Order item not found", HttpStatus.NOT_FOUND),
    ORDER_ITEM_INVALID(1008, "Order item invalid", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1009, "Order not found", HttpStatus.NOT_FOUND),
    TRANSACTION_NOT_FOUND(1010, "Transaction not found", HttpStatus.NOT_FOUND),
    TRANSACTION_FAILED(1011, "Transaction failed", HttpStatus.BAD_REQUEST),
    SHEET_NOT_FOUND(1012, "Excel sheet not found", HttpStatus.NOT_FOUND),
    UNKNOWN_FILE_TYPE(1013, "Unknown file type", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;
}