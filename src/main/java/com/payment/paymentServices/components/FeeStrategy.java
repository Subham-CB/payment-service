package com.payment.paymentServices.components;

public interface FeeStrategy {
    double calculateFee(double amount);
    String getType();
}
