package com.example.backend.utils;

import com.example.backend.enums.PaymentMethod;

import java.util.Random;

public class PaymentSimulator {

    private static final Random random = new Random();

    public static boolean mockPayment(PaymentMethod method) {
        System.out.println("Processing: " + method);

        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return switch (method) {
            case CASH -> true;
            case CARD -> random.nextDouble() < 0.9;
            case E_WALLET -> random.nextDouble() < 0.8;
            case UNKNOWN -> random.nextDouble() < 0.02;
            default -> false;
        };
    }
}

