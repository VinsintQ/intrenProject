-- Create limit_orders table
CREATE TABLE IF NOT EXISTS limit_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    crypto_id VARCHAR(255) NOT NULL,
    crypto_symbol VARCHAR(50) NOT NULL,
    crypto_name VARCHAR(255) NOT NULL,
    order_type VARCHAR(10) NOT NULL,
    limit_price DOUBLE NOT NULL,
    quantity DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    expiry_date TIMESTAMP NULL,
    filled_date TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for faster queries
CREATE INDEX idx_limit_orders_user_id ON limit_orders(user_id);
CREATE INDEX idx_limit_orders_status ON limit_orders(status);
CREATE INDEX idx_limit_orders_crypto_id ON limit_orders(crypto_id);
