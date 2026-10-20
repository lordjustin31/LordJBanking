package com.ciicc.lordjbank.model;

import java.math.BigDecimal;

public class TransferTransaction extends Transaction {

    public TransferTransaction(String accountNumber, BigDecimal amount,
                               BigDecimal balanceAfter, String referenceAccount) {
        super(accountNumber, amount, balanceAfter, referenceAccount);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TRANSFER;
    }
}