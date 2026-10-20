package com.ciicc.orozbank.repository;

import com.ciicc.orozbank.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    List<Transaction> findByAccountNumber(String accountNumber);
}