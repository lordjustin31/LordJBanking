package com.ciicc.lordjbank.repository;

import com.ciicc.lordjbank.model.DepositTransaction;
import com.ciicc.lordjbank.model.Transaction;
import com.ciicc.lordjbank.model.TransactionType;
import com.ciicc.lordjbank.model.TransferTransaction;
import com.ciicc.lordjbank.model.WithdrawTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private final JdbcTemplate jdbc;

    public JdbcTransactionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(Transaction transaction) {

        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO transactions " +
                            "(account_number, transaction_type, amount, balance, reference_account) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, transaction.getAccountNumber());
            ps.setString(2, transaction.getType().name());
            ps.setBigDecimal(3, transaction.getAmount());
            ps.setBigDecimal(4, transaction.getBalanceAfter());
            ps.setString(5, transaction.getReferenceAccount());
            return ps;
        }, keys);

        long id = keys.getKey().longValue();
        String reference = String.format("TXN-%03d", id);
        jdbc.update("UPDATE transactions SET transaction_reference = ? WHERE id = ?", reference, id);

        transaction.setId(id);
        transaction.setReference(reference);
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return jdbc.query(
                "SELECT id, transaction_reference, account_number, transaction_type, amount, " +
                        "balance, reference_account, created_at " +
                        "FROM transactions WHERE account_number = ? ORDER BY id",
                this::mapRow, accountNumber);
    }

    private Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        String accountNumber = rs.getString("account_number");
        BigDecimal amount = rs.getBigDecimal("amount");
        BigDecimal balance = rs.getBigDecimal("balance");
        String referenceAccount = rs.getString("reference_account");

        TransactionType type = TransactionType.valueOf(rs.getString("transaction_type"));
        Transaction txn = switch (type) {
            case DEPOSIT -> new DepositTransaction(accountNumber, amount, balance);
            case WITHDRAW -> new WithdrawTransaction(accountNumber, amount, balance);
            case TRANSFER -> new TransferTransaction(accountNumber, amount, balance, referenceAccount);
        };

        txn.setId(rs.getLong("id"));
        txn.setReference(rs.getString("transaction_reference"));

        Timestamp created = rs.getTimestamp("created_at");
        txn.setCreatedAt(created == null ? null : created.toLocalDateTime());
        return txn;
    }
}