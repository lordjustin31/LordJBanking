package com.ciicc.lordjbank.model;

import java.math.BigDecimal;

public class DepositTransaction extends Transaction {

    public DepositTransaction(String accountNumber, BigDecimal amount, BigDecimal balanceAfter) {
        super(accountNumber, amount, balanceAfter, null);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.DEPOSIT;
    }
}