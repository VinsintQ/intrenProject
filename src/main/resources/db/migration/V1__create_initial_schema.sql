-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTOINCREMENT,
    user_name VARCHAR(255),
    email_address VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    account_verified BOOLEAN NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT 1,
    balance DOUBLE NOT NULL DEFAULT 0.0
);

-- Create crypto_holdings table
CREATE TABLE IF NOT EXISTS crypto_holdings (
    id BIGINT PRIMARY KEY AUTOINCREMENT,
    user_id BIGINT NOT NULL,
    crypto_id VARCHAR(255) NOT NULL,
    crypto_symbol VARCHAR(50) NOT NULL,
    quantity DOUBLE NOT NULL,
    average_buy_price DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create crypto_transactions table
CREATE TABLE IF NOT EXISTS crypto_transactions (
    id BIGINT PRIMARY KEY AUTOINCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    crypto_id VARCHAR(255) NOT NULL,
    crypto_symbol VARCHAR(50) NOT NULL,
    quantity DOUBLE NOT NULL,
    price_per_unit DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create deposits table
CREATE TABLE IF NOT EXISTS deposits (
    id BIGINT PRIMARY KEY AUTOINCREMENT,
    user_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    currency VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_intent_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_crypto_holdings_user_id ON crypto_holdings(user_id);
CREATE INDEX idx_crypto_transactions_user_id ON crypto_transactions(user_id);
CREATE INDEX idx_deposits_user_id ON deposits(user_id);
