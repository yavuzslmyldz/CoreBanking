package com.tuum.core.banking.repository;

import com.tuum.core.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {
}