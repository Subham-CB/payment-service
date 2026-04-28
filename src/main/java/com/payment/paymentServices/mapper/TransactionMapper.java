package com.payment.paymentServices.mapper;

import com.payment.paymentServices.components.PaymentMethod;
import com.payment.paymentServices.dto.PaymentResponseDto;
import com.payment.paymentServices.entity.Transaction;
import com.payment.paymentServices.entity.TransactionStatus;
import com.payment.paymentServices.enums.TransactionStatusEnum;

import java.util.UUID;

public class TransactionMapper {


    public static Transaction toEntity(double amount,double fee, PaymentMethod payMethod, UUID id, TransactionStatus status){


        Transaction transaction = new Transaction();

        transaction.setId(id.toString());
        transaction.setAmount(amount);
        transaction.setTransactionType(payMethod.getType());
        transaction.setFee(fee);
        transaction.setTotalAmount(amount+fee);

        transaction.setStatus(status);

        return transaction;

    }

    public static PaymentResponseDto toResponseDto(Transaction transaction){

        return new PaymentResponseDto(transaction.getId(), transaction.getStatus().getName());

    }

}
