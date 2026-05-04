package com.payment.paymentServices.mapper;

import com.payment.paymentServices.components.PaymentMethod;
import com.payment.paymentServices.dto.PaymentResponseDto;
import com.payment.paymentServices.entity.Transaction;
import com.payment.paymentServices.entity.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionMapperTest {

    private PaymentMethod paymentMethod;
    private TransactionStatus transactionStatus;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        paymentMethod = mock(PaymentMethod.class);
        when(paymentMethod.getType()).thenReturn("CARD");

        transactionStatus = new TransactionStatus();
        transactionStatus.setId(1L);
        transactionStatus.setName("SUCCESS");

        transactionId = UUID.randomUUID();
    }

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        double amount = 100.0;
        double fee = 5.0;

        Transaction transaction = TransactionMapper.toEntity(amount, fee, paymentMethod, transactionId, transactionStatus);

        assertThat(transaction.getId()).isEqualTo(transactionId.toString());
        assertThat(transaction.getAmount()).isCloseTo(amount, within(0.001));
        assertThat(transaction.getFee()).isCloseTo(fee, within(0.001));
        assertThat(transaction.getTotalAmount()).isCloseTo(amount + fee, within(0.001));
        assertThat(transaction.getTransactionType()).isEqualTo("CARD");
        assertThat(transaction.getStatus()).isEqualTo(transactionStatus);
    }

    @Test
    void toResponseDto_shouldMapIdAndStatus() {
        Transaction transaction = new Transaction();
        transaction.setId(transactionId.toString());
        transaction.setStatus(transactionStatus);

        PaymentResponseDto dto = TransactionMapper.toResponseDto(transaction);

        assertThat(dto.transactionId()).isEqualTo(transactionId.toString());
        assertThat(dto.status()).isEqualTo("SUCCESS");
    }
}
