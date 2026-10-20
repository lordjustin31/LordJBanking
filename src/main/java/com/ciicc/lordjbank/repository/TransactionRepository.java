package com.ciicc.lordjbank.repository;

import com.ciicc.lordjbank.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    List<Transaction> findByAccountNumber(String accountNumber);
}