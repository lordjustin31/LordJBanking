package com.ciicc.lordjbank.service;

import com.ciicc.lordjbank.exception.BankException;
import com.ciicc.lordjbank.model.Account;
import com.ciicc.lordjbank.model.DepositTransaction;
import com.ciicc.lordjbank.model.Transaction;
import com.ciicc.lordjbank.model.TransferTransaction;
import com.ciicc.lordjbank.model.WithdrawTransaction;
import com.ciicc.lordjbank.repository.AccountRepository;
import com.ciicc.lordjbank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {

    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public BankService(AccountRepository accounts, TransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Transactional
    public void createAccount(String accountNumber, String accountName, BigDecimal initialDeposit) {
        Account account = new Account(accountNumber, accountName, initialDeposit);

        if (accounts.existsByAccountNumber(account.getAccountNumber())) {
            throw new BankException("Account number " + account.getAccountNumber() + " already exists.");
        }

        accounts.save(account);
        transactions.save(new DepositTransaction(
                account.getAccountNumber(), account.getBalance(), account.getBalance()));
    }

    public Account getAccount(String accountNumber) {
        return findOrThrow(accountNumber);
    }

    public List<Account> listAccounts() {
        return accounts.findAll();
    }

    @Transactional
    public BigDecimal deposit(String accountNumber, BigDecimal amount) {
        Account account = lockOrThrow(accountNumber);
        account.deposit(amount);                       // the Account checks the rules
        accounts.updateBalance(account.getAccountNumber(), account.getBalance());
        transactions.save(new DepositTransaction(account.getAccountNumber(), amount, account.getBalance()));
        return account.getBalance();
    }

    @Transactional
    public BigDecimal withdraw(String accountNumber, BigDecimal amount) {
        Account account = lockOrThrow(accountNumber);
        account.withdraw(amount);
        accounts.updateBalance(account.getAccountNumber(), account.getBalance());
        transactions.save(new WithdrawTransaction(account.getAccountNumber(), amount, account.getBalance()));
        return account.getBalance();
    }

    @Transactional
    public void transfer(String fromNumber, String toNumber, BigDecimal amount) {
        String from = fromNumber.trim();
        String to = toNumber.trim();

        if (from.equals(to)) {
            throw new BankException("Source and destination accounts must be different.");
        }

        Account source = lockOrThrow(from);
        Account destination = lockOrThrow(to);

        source.withdraw(amount);
        destination.deposit(amount);

        accounts.updateBalance(from, source.getBalance());
        accounts.updateBalance(to, destination.getBalance());

        transactions.save(new TransferTransaction(from, amount, source.getBalance(), to));
        transactions.save(new TransferTransaction(to, amount, destination.getBalance(), from));
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        findOrThrow(accountNumber); // make sure the account exists first
        return transactions.findByAccountNumber(accountNumber.trim());
    }


    private Account findOrThrow(String accountNumber) {
        return accounts.findByAccountNumber(accountNumber.trim())
                .orElseThrow(() -> new BankException("Account " + accountNumber.trim() + " does not exist."));
    }

    private Account lockOrThrow(String accountNumber) {
        return accounts.findByAccountNumberForUpdate(accountNumber.trim())
                .orElseThrow(() -> new BankException("Account " + accountNumber.trim() + " does not exist."));
    }
}