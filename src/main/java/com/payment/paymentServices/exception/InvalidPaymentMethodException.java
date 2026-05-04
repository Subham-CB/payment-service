package com.payment.paymentServices.exception;

public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String type) {
        super("Unsupported payment method or fee strategy: " + type);
    }
}
