package com.ciicc.orozbank.repository;

import com.ciicc.orozbank.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    void save(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumberForUpdate(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findAll();

    void updateBalance(String accountNumber, BigDecimal balance);
}