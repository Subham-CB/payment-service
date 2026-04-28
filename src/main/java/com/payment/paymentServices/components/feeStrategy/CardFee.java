package com.payment.paymentServices.components.feeStrategy;

import com.payment.paymentServices.components.FeeStrategy;
import org.springframework.stereotype.Component;

@Component
public class CardFee implements FeeStrategy {
    @Override
    public double calculateFee(double amount) {
        return amount*0.05;
    }

    @Override
    public String getType() {
        return "CARD";
    }
}
