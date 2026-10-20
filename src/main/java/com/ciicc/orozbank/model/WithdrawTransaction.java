package com.ciicc.orozbank.model;

import java.math.BigDecimal;

public class WithdrawTransaction extends Transaction {

    public WithdrawTransaction(String accountNumber, BigDecimal amount, BigDecimal balanceAfter) {
        super(accountNumber, amount, balanceAfter, null);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WITHDRAW;
    }
}