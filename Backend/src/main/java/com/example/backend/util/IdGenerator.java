package com.example.backend.util;

import java.util.UUID;

public class IdGenerator {

    public static String generateId(String prefix) {
        String random = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 6)
                .toUpperCase();
        return prefix + "-" + random;
    }
}
