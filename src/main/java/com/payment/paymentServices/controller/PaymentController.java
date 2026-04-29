package com.payment.paymentServices.controller;

import com.payment.paymentServices.dto.PaymentRequestDto;
import com.payment.paymentServices.dto.PaymentResponseDto;
import com.payment.paymentServices.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto){
        return ResponseEntity.ok(paymentService.processPayment(paymentRequestDto));
    }
}
