package com.payment.paymentServices.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaymentRequestDto(

        @NotBlank(message = "Transaction type cannot be blank")
        String transactionType,
        @Min(value = 1, message = "The amount must be at least 1")
        double amount ) {
}
