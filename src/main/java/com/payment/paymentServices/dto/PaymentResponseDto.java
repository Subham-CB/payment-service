package com.payment.paymentServices.dto;

import com.payment.paymentServices.enums.TransactionStatusEnum;

public record PaymentResponseDto(String transactionId, String status) {

}
