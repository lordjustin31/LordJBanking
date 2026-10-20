package com.ciicc.lordjbank.repository;

import com.ciicc.lordjbank.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAccountRepository implements AccountRepository {

    private static final String SELECT_SQL =
            "SELECT id, account_number, account_name, balance, created_at FROM accounts";

    private final JdbcTemplate jdbc;

    public JdbcAccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(Account account) {
        jdbc.update("INSERT INTO accounts (account_number, account_name, balance) VALUES (?, ?, ?)",
                account.getAccountNumber(), account.getAccountName(), account.getBalance());
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jdbc.query(SELECT_SQL + " WHERE account_number = ?", this::mapRow, accountNumber)
                .stream().findFirst();
    }

    @Override
    public Optional<Account> findByAccountNumberForUpdate(String accountNumber) {
        return jdbc.query(SELECT_SQL + " WHERE account_number = ? FOR UPDATE", this::mapRow, accountNumber)
                .stream().findFirst();
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM accounts WHERE account_number = ?", Integer.class, accountNumber);
        return count != null && count > 0;
    }

    @Override
    public List<Account> findAll() {
        return jdbc.query(SELECT_SQL + " ORDER BY account_number", this::mapRow);
    }

    @Override
    public void updateBalance(String accountNumber, BigDecimal balance) {
        jdbc.update("UPDATE accounts SET balance = ? WHERE account_number = ?", balance, accountNumber);
    }

    private Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account(
                rs.getString("account_number"),
                rs.getString("account_name"),
                rs.getBigDecimal("balance"));
        account.setId(rs.getLong("id"));

        Timestamp created = rs.getTimestamp("created_at");
        account.setCreatedAt(created == null ? null : created.toLocalDateTime());
        return account;
    }
}