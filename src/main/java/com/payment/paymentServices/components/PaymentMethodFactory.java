package com.payment.paymentServices.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodFactory {

    private final List<PaymentMethod> methodList;



    public PaymentMethod getPayMethod(String type){

        methodList.forEach(m->log.info("method type: {}" ,m.getType()));

        return methodList.stream()
                .filter(m->m.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(()->new RuntimeException("Payment method not found"));
    }
}
