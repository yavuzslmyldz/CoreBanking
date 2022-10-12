package com.tuum.core.banking.repository;

import com.tuum.core.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(long accountId);

    @Query(value = "SELECT \n" +
            "\tCASE\n" +
            "    WHEN (SELECT COUNT(*) FROM transaction) = 0 THEN last_value\n" +
            "    ELSE last_value + 1 \n" +
            "  END \n" +
            "  AS val\n" +
            "FROM transaction_seq", nativeQuery = true)
    Long getNextTransactionId();
}
