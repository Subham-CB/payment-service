package com.payment.paymentServices.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeeStrategyFactory {

    private final List<FeeStrategy> feeStrategies;

    public FeeStrategy getFee(String type){

        return feeStrategies.stream()
                .filter(f-> f.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No Fee Strategy found"));
    }

}
