package com.payment.paymentServices.components;

public interface PaymentMethod {
    void pay(double amount);
    String getType();
}
