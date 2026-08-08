-- 1. WALLETS TABLE
CREATE TABLE wallets (
                         id UUID PRIMARY KEY,
                         user_id UUID NOT NULL,
                         balance_amount DECIMAL(19, 4) NOT NULL,
                         balance_currency VARCHAR(3) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                         version BIGINT NOT NULL DEFAULT 0,
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallets_user_id ON wallets(user_id);

-- 2. TRANSACTIONS TABLE
CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              source_wallet_id UUID,
                              target_wallet_id UUID,
                              amount_value DECIMAL(19, 4) NOT NULL,
                              amount_currency VARCHAR(3) NOT NULL,
                              type VARCHAR(20) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              idempotency_key VARCHAR(100) NOT NULL UNIQUE,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_source_wallet ON transactions(source_wallet_id);
CREATE INDEX idx_transactions_target_wallet ON transactions(target_wallet_id);
CREATE INDEX idx_transactions_idempotency_key ON transactions(idempotency_key);

-- 3. IDEMPOTENCY KEYS TABLE
CREATE TABLE idempotency_keys (
                                  key VARCHAR(100) PRIMARY KEY,
                                  response_payload TEXT,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. AUDIT LOGS TABLE (IMMUTABLE — INSERT ONLY)
CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY,
                            wallet_id UUID NOT NULL,
                            transaction_id UUID,
                            operation_type VARCHAR(20) NOT NULL,
                            previous_balance DECIMAL(19, 4) NOT NULL,
                            new_balance DECIMAL(19, 4) NOT NULL,
                            currency VARCHAR(3) NOT NULL,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_wallet_id ON audit_logs(wallet_id);