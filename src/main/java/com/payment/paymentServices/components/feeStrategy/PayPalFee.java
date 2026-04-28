package com.payment.paymentServices.components.feeStrategy;

import com.payment.paymentServices.components.FeeStrategy;


public class PayPalFee implements FeeStrategy {
    @Override
    public double calculateFee(double amount) {
        return amount*0.1;
    }

    @Override
    public String getType() {
        return "PAYPAL";
    }
}
