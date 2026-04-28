package com.payment.paymentServices.service;

import com.payment.paymentServices.dto.PaymentRequestDto;
import com.payment.paymentServices.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto);

}
