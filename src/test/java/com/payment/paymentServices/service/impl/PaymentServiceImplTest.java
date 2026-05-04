package com.payment.paymentServices.service.impl;

import com.payment.paymentServices.components.FeeStrategy;
import com.payment.paymentServices.components.FeeStrategyFactory;
import com.payment.paymentServices.components.PaymentMethod;
import com.payment.paymentServices.components.PaymentMethodFactory;
import com.payment.paymentServices.dto.PaymentRequestDto;
import com.payment.paymentServices.dto.PaymentResponseDto;
import com.payment.paymentServices.entity.Transaction;
import com.payment.paymentServices.entity.TransactionStatus;
import com.payment.paymentServices.enums.TransactionStatusEnum;
import com.payment.paymentServices.exception.InvalidPaymentMethodException;
import com.payment.paymentServices.exception.PaymentStatusNotFoundException;
import com.payment.paymentServices.repository.TransactionRepository;
import com.payment.paymentServices.repository.TransactionStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionStatusRepository transactionStatusRepository;

    @Mock
    private PaymentMethodFactory paymentMethodFactory;

    @Mock
    private FeeStrategyFactory feeStrategyFactory;

    @Mock
    private PaymentMethod paymentMethod;

    @Mock
    private FeeStrategy feeStrategy;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private TransactionStatus successStatus;
    private TransactionStatus failedStatus;

    @BeforeEach
    void setUp() {
        successStatus = new TransactionStatus();
        successStatus.setId(1L);
        successStatus.setName(TransactionStatusEnum.SUCCESS.name());

        failedStatus = new TransactionStatus();
        failedStatus.setId(2L);
        failedStatus.setName(TransactionStatusEnum.FAILED.name());
    }

    @Test
    void processPayment_shouldReturnSuccessResponse_whenPaymentSucceeds() {
        PaymentRequestDto request = new PaymentRequestDto("CARD", 100.0);

        when(paymentMethodFactory.getPayMethod("CARD")).thenReturn(paymentMethod);
        when(feeStrategyFactory.getFee("CARD")).thenReturn(feeStrategy);
        when(feeStrategy.calculateFee(100.0)).thenReturn(5.0);
        when(paymentMethod.getType()).thenReturn("CARD");
        doNothing().when(paymentMethod).pay(105.0);
        when(transactionStatusRepository.findByName(TransactionStatusEnum.SUCCESS.name()))
                .thenReturn(Optional.of(successStatus));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId("some-uuid");
        savedTransaction.setStatus(successStatus);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        PaymentResponseDto response = paymentService.processPayment(request);

        assertThat(response.status()).isEqualTo(TransactionStatusEnum.SUCCESS.name());
        assertThat(response.transactionId()).isEqualTo("some-uuid");
        verify(paymentMethod).pay(105.0);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processPayment_shouldReturnFailedResponse_whenPaymentMethodThrows() {
        PaymentRequestDto request = new PaymentRequestDto("CARD", 200.0);

        when(paymentMethodFactory.getPayMethod("CARD")).thenReturn(paymentMethod);
        when(feeStrategyFactory.getFee("CARD")).thenReturn(feeStrategy);
        when(feeStrategy.calculateFee(200.0)).thenReturn(10.0);
        when(paymentMethod.getType()).thenReturn("CARD");
        doThrow(new RuntimeException("Payment Failed")).when(paymentMethod).pay(210.0);
        when(transactionStatusRepository.findByName(TransactionStatusEnum.FAILED.name()))
                .thenReturn(Optional.of(failedStatus));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId("some-uuid");
        savedTransaction.setStatus(failedStatus);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        PaymentResponseDto response = paymentService.processPayment(request);

        assertThat(response.status()).isEqualTo(TransactionStatusEnum.FAILED.name());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processPayment_shouldPersistCorrectFeeAndTotalAmount() {
        PaymentRequestDto request = new PaymentRequestDto("CARD", 100.0);

        when(paymentMethodFactory.getPayMethod("CARD")).thenReturn(paymentMethod);
        when(feeStrategyFactory.getFee("CARD")).thenReturn(feeStrategy);
        when(feeStrategy.calculateFee(100.0)).thenReturn(5.0);
        when(paymentMethod.getType()).thenReturn("CARD");
        doNothing().when(paymentMethod).pay(anyDouble());
        when(transactionStatusRepository.findByName(TransactionStatusEnum.SUCCESS.name()))
                .thenReturn(Optional.of(successStatus));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId("some-uuid");
        savedTransaction.setStatus(successStatus);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        paymentService.processPayment(request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction captured = captor.getValue();

        assertThat(captured.getAmount()).isCloseTo(100.0, within(0.001));
        assertThat(captured.getFee()).isCloseTo(5.0, within(0.001));
        assertThat(captured.getTotalAmount()).isCloseTo(105.0, within(0.001));
        assertThat(captured.getTransactionType()).isEqualTo("CARD");
    }

    @Test
    void processPayment_shouldThrowInvalidPaymentMethodException_whenTypeIsUnknown() {
        PaymentRequestDto request = new PaymentRequestDto("CRYPTO", 100.0);
        when(paymentMethodFactory.getPayMethod("CRYPTO"))
                .thenThrow(new InvalidPaymentMethodException("CRYPTO"));

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(InvalidPaymentMethodException.class)
                .hasMessageContaining("CRYPTO");

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void processPayment_shouldThrowPaymentStatusNotFoundException_whenStatusMissingFromDb() {
        PaymentRequestDto request = new PaymentRequestDto("CARD", 100.0);

        when(paymentMethodFactory.getPayMethod("CARD")).thenReturn(paymentMethod);
        when(feeStrategyFactory.getFee("CARD")).thenReturn(feeStrategy);
        when(feeStrategy.calculateFee(100.0)).thenReturn(5.0);
        doNothing().when(paymentMethod).pay(anyDouble());
        when(transactionStatusRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(PaymentStatusNotFoundException.class);

        verifyNoInteractions(transactionRepository);
    }
}
