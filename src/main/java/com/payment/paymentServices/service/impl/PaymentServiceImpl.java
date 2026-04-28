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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
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

        } catch (Exception e) {

            statusEnum = TransactionStatusEnum.FAILED;

        }

        TransactionStatus statusEntity = transactionStatusRepository
                .findByName(statusEnum.name())
                .orElseThrow(()->new RuntimeException("Status not found in DB: "));

        Transaction transaction = TransactionMapper.toEntity(amount,fee,payMethod,transactionId,statusEntity);


        return TransactionMapper.toResponseDto(transactionRepository.save(transaction));
    }
}
