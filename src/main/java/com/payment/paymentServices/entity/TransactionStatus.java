package com.payment.paymentServices.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TransactionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true,nullable = false)
    private String name;

}
