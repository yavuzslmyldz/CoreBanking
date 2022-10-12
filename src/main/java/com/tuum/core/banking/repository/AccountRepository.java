package com.tuum.core.banking.repository;

import com.tuum.core.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query(value = "SELECT \n" +
            "\tCASE\n" +
            "    WHEN (SELECT COUNT(*) FROM account) = 0 THEN last_value\n" +
            "    ELSE last_value + 1 \n" +
            "  END \n" +
            "  AS val\n" +
            "FROM account_seq", nativeQuery = true)
    Long getNextAccountId();
}