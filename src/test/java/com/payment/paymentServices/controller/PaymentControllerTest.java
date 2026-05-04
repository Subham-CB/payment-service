package com.payment.paymentServices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentServices.dto.PaymentRequestDto;
import com.payment.paymentServices.dto.PaymentResponseDto;
import com.payment.paymentServices.exception.InvalidPaymentMethodException;
import com.payment.paymentServices.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processPayment_shouldReturn200_whenRequestIsValid() throws Exception {
        PaymentRequestDto request = new PaymentRequestDto("CARD", 100.0);
        PaymentResponseDto response = new PaymentResponseDto("tx-id-123", "SUCCESS");

        when(paymentService.processPayment(any(PaymentRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-id-123"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void processPayment_shouldReturn400_whenTransactionTypeIsBlank() throws Exception {
        String body = "{\"transactionType\":\"\",\"amount\":100.0}";

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_shouldReturn400_whenAmountIsBelowMinimum() throws Exception {
        String body = "{\"transactionType\":\"CARD\",\"amount\":0}";

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_shouldReturn400_whenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_shouldReturn400_whenPaymentMethodIsInvalid() throws Exception {
        PaymentRequestDto request = new PaymentRequestDto("BITCOIN", 100.0);

        when(paymentService.processPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentMethodException("BITCOIN"));

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported payment method or fee strategy: BITCOIN"));
    }

    @Test
    void processPayment_shouldReturn405_whenGetMethodIsUsed() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/payment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
}
