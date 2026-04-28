package com.payment.paymentServices.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMethodFactory {

    private final List<PaymentMethod> methodList;

    public PaymentMethod getPayMethod(String type){
        return methodList.stream()
                .filter(m->m.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(()->new RuntimeException("Payment method not found"));
    }
}
