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
import com.payment.paymentServices.mapper.TransactionMapper;
import com.payment.paymentServices.repository.TransactionRepository;
import com.payment.paymentServices.repository.TransactionStatusRepository;
import com.payment.paymentServices.service.PaymentService;
import com.payment.paymentServices.exception.PaymentStatusNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;
    private final TransactionStatusRepository transactionStatusRepository;
    private final PaymentMethodFactory paymentMethodFactory;
    private final FeeStrategyFactory feeStrategyFactory;

    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto) {

        PaymentMethod payMethod = paymentMethodFactory.getPayMethod(paymentRequestDto.transactionType());
        FeeStrategy feeStrategy = feeStrategyFactory.getFee(paymentRequestDto.transactionType());


        double amount = paymentRequestDto.amount();
        double fee = feeStrategy.calculateFee(paymentRequestDto.amount());
        UUID transactionId = UUID.randomUUID();

        TransactionStatusEnum statusEnum;

        try{
            payMethod.pay(amount+fee);

            statusEnum = TransactionStatusEnum.SUCCESS;

            log.info("Payment processing completed for transaction = {}",transactionId);

        } catch (Exception e) {

            log.warn("Payment processing failed for transaction = {} : {}",transactionId,e.getMessage());

            statusEnum = TransactionStatusEnum.FAILED;

        }

        final TransactionStatusEnum resolvedStatus = statusEnum;

        TransactionStatus statusEntity = transactionStatusRepository
                .findByName(statusEnum.name())
                .orElseThrow(()->new PaymentStatusNotFoundException(resolvedStatus.name()));

        Transaction transaction = TransactionMapper.toEntity(amount,fee,payMethod,transactionId,statusEntity);


        return TransactionMapper.toResponseDto(transactionRepository.save(transaction));
    }
}
