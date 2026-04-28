package com.payment.paymentServices.repository;

import com.payment.paymentServices.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionStatusRepository extends JpaRepository<TransactionStatus,Long> {

    // You must declare this to search by the 'name' field
    Optional<TransactionStatus> findByName(String name);

    // You can also add this for your Startup Runner
    boolean existsByName(String name);
}
