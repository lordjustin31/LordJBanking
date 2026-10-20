package com.ciicc.lordjbank.console;

import com.ciicc.lordjbank.exception.BankException;
import com.ciicc.lordjbank.model.Account;
import com.ciicc.lordjbank.model.Transaction;
import com.ciicc.lordjbank.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BankService bankService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public void run(String... args) {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = ask("Enter your choice: ");

            try {
                switch (choice) {
                    case "1" -> createAccount();
                    case "2" -> balanceInquiry();
                    case "3" -> listAccounts();
                    case "4" -> deposit();
                    case "5" -> withdraw();
                    case "6" -> transfer();
                    case "7" -> transactionHistory();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice. Enter a number from 0 to 7.");
                }
            } catch (BankException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (DataAccessException e) {
                System.out.println("DATABASE ERROR: " + e.getMostSpecificCause().getMessage());
            }

            if (running) {
                ask("\nPress Enter to return to the menu...");
                System.out.println();
            }
        }
        System.out.println("Goodbye!");
    }


    private void createAccount() {
        String number = ask("Account Number: ");
        String name = ask("Account Name: ");
        BigDecimal initial = readAmount("Initial Deposit: ");

        bankService.createAccount(number, name, initial);

        System.out.println("Account created successfully!");
        printAccount(bankService.getAccount(number));
    }

    private void balanceInquiry() {
        printAccount(bankService.getAccount(ask("Account Number: ")));
    }

    private void listAccounts() {
        List<Account> accounts = bankService.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered.");
            return;
        }

        System.out.printf("%-15s %-30s %18s%n", "Account No.", "Account Holder", "Balance");
        System.out.println("-".repeat(65));
        for (Account a : accounts) {
            System.out.printf("%-15s %-30s %18s%n",
                    a.getAccountNumber(), a.getAccountName(), formatMoney(a.getBalance()));
        }
        System.out.println("-".repeat(65));
        System.out.println("Total accounts: " + accounts.size());
    }

    private void deposit() {
        String number = ask("Account Number: ");
        BigDecimal amount = readAmount("Deposit Amount: ");

        BigDecimal previous = bankService.getAccount(number).getBalance();
        BigDecimal newBalance = bankService.deposit(number, amount);

        System.out.println("Previous Balance: " + formatMoney(previous));
        System.out.println("Deposit Amount:   " + formatMoney(amount));
        System.out.println("New Balance:      " + formatMoney(newBalance));
        System.out.println("");
        System.out.println("Deposit successful!");
    }

    private void withdraw() {
        String number = ask("Account Number: ");
        BigDecimal amount = readAmount("Withdrawal Amount: ");

        BigDecimal previous = bankService.getAccount(number).getBalance();
        BigDecimal newBalance = bankService.withdraw(number, amount);

        System.out.println("Previous Balance:  " + formatMoney(previous));
        System.out.println("Withdrawal Amount: " + formatMoney(amount));
        System.out.println("New Balance:       " + formatMoney(newBalance));
        System.out.println("");
        System.out.println("Withdrawal successful!");
    }

    private void transfer() {
        String from = ask("From Account Number: ");
        String to = ask("To Account Number: ");
        BigDecimal amount = readAmount("Transfer Amount: ");

        bankService.transfer(from, to, amount);

        System.out.println("Transfer successful!");
        System.out.println("From " + from.trim() + " new balance: "
                + formatMoney(bankService.getAccount(from).getBalance()));
        System.out.println("To   " + to.trim() + " new balance: "
                + formatMoney(bankService.getAccount(to).getBalance()));
    }

    private void transactionHistory() {
        String number = ask("Account Number: ");
        List<Transaction> history = bankService.getTransactionHistory(number);

        if (history.isEmpty()) {
            System.out.println("No transactions found for " + number.trim() + ".");
            return;
        }

        System.out.printf("%-10s %-10s %15s %15s %-20s %-18s%n",
                "Reference", "Type", "Amount", "Balance", "Date", "Reference Account");
        System.out.println("-".repeat(92));
        for (Transaction t : history) {
            System.out.printf("%-10s %-10s %15s %15s %-20s %-18s%n",
                    t.getReference(),
                    t.getType(),                      // POLYMORPHISM: works for every subclass
                    formatMoney(t.getAmount()),
                    formatMoney(t.getBalanceAfter()),
                    t.getCreatedAt() == null ? "-" : t.getCreatedAt().format(DATE_FORMAT),
                    t.getReferenceAccount() == null ? "-" : t.getReferenceAccount());
        }
    }


    private void printMenu() {
        System.out.println("====================================");
        System.out.println(" BANKING APPLICATION");
        System.out.println("====================================");
        System.out.println("1. Create Account");
        System.out.println("2. Balance Inquiry");
        System.out.println("3. List Accounts");
        System.out.println("4. Deposit");
        System.out.println("5. Withdraw");
        System.out.println("6. Transfer");
        System.out.println("7. Transaction History");
        System.out.println("0. Exit");
    }

    private void printAccount(Account a) {
        System.out.println("Account Number:  " + a.getAccountNumber());
        System.out.println("Account Holder:  " + a.getAccountName());
        System.out.println("Current Balance: " + formatMoney(a.getBalance()));
    }

    private String ask(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private BigDecimal readAmount(String message) {
        String text = ask(message);
        try {
            return new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new BankException("'" + text + "' is not a valid amount.");
        }
    }

    private String formatMoney(BigDecimal value) {
        return String.format("PHP %,.2f", value);
    }
}