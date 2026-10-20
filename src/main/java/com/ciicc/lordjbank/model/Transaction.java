package com.ciicc.lordjbank.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public abstract class Transaction extends BaseEntity {

    private final String accountNumber;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final String referenceAccount;

    @Getter
    @Setter
    private String reference;

    protected Transaction(String accountNumber, BigDecimal amount,
                          BigDecimal balanceAfter, String referenceAccount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.referenceAccount = referenceAccount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getReferenceAccount() {
        return referenceAccount;
    }

    public abstract TransactionType getType();
}