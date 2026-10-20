DROP DATABASE IF EXISTS banking_app;
CREATE DATABASE banking_app;
USE banking_app;

CREATE TABLE accounts (
                          id             INT AUTO_INCREMENT PRIMARY KEY,
                          account_number VARCHAR(20)   NOT NULL UNIQUE,
                          account_name   VARCHAR(100)  NOT NULL,
                          balance        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                          created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE transactions (
                              id                    INT AUTO_INCREMENT PRIMARY KEY,
                              transaction_reference VARCHAR(20)   UNIQUE,
                              account_number        VARCHAR(20)   NOT NULL,
                              transaction_type      ENUM('DEPOSIT','WITHDRAW','TRANSFER') NOT NULL,
                              amount                DECIMAL(12,2) NOT NULL,
                              balance               DECIMAL(12,2) NOT NULL,
                              reference_account     VARCHAR(20),
                              created_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (account_number)    REFERENCES accounts(account_number),
                              FOREIGN KEY (reference_account) REFERENCES accounts(account_number)
) ENGINE=InnoDB;
