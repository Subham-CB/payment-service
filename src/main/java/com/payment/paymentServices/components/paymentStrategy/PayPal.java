package com.payment.paymentServices.components.paymentStrategy;

import com.payment.paymentServices.components.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PayPal implements PaymentMethod {
    @Override
    public void pay(double amount) {
        if (Math.random() <0.8){
            throw new RuntimeException("Payment Failed");
        }
    }

    @Override
    public String getType() {
        return "PAYPAL";
    }
}
