package com.payment.paymentServices.exception;

public class PaymentStatusNotFoundException extends RuntimeException {
    public PaymentStatusNotFoundException(String status) {
        super("Transaction status not found in DB" + status);
    }
}
