CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       encrypted_iban_data BYTEA,
                       encrypted_iban_key BYTEA,
                       iban_iv BYTEA,
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
