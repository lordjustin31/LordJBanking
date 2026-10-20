package com.ciicc.lordjbank.model;

import com.ciicc.lordjbank.exception.BankException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account extends BaseEntity {

    private final String accountNumber;
    private final String accountName;

    @Getter
    private BigDecimal balance;

    public Account(String accountNumber, String accountName, BigDecimal balance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new BankException("Account number must not be empty.");
        }
        if (accountName == null || accountName.isBlank()) {
            throw new BankException("Account holder name must not be empty.");
        }
        if (balance == null || balance.signum() < 0) {
            throw new BankException("Initial deposit must be zero or greater.");
        }

        this.accountNumber = accountNumber.trim();
        this.accountName = accountName.trim();
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (amount.compareTo(balance) > 0) {
            throw new BankException("Insufficient funds. Current balance: " + balance);
        }
        balance = balance.subtract(amount);
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BankException("Amount must be greater than zero.");
        }
    }
}