-- =============================================================
-- CardDemo Application — H2 Database Schema
-- =============================================================

-- ---------------------------------------------------------------
-- TABLE: users
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id       VARCHAR(64)   NOT NULL,
    first_name    VARCHAR(100)  NOT NULL,
    last_name     VARCHAR(100)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    user_type     VARCHAR(20)   NOT NULL,
    status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(64)   NOT NULL DEFAULT 'SYSTEM',
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(64)   NOT NULL DEFAULT 'SYSTEM',
    version       BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT chk_users_user_type CHECK (user_type IN ('ADMIN', 'STANDARD')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED'))
);

CREATE INDEX IF NOT EXISTS idx_users_type   ON users (user_type);
CREATE INDEX IF NOT EXISTS idx_users_status ON users (status);

-- ---------------------------------------------------------------
-- TABLE: customers
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
    customer_id                  VARCHAR(32)   NOT NULL,
    ssn                          VARCHAR(11),
    credit_score                 INTEGER,
    date_of_birth                DATE,
    first_name                   VARCHAR(100)  NOT NULL,
    middle_name                  VARCHAR(100),
    last_name                    VARCHAR(100)  NOT NULL,
    address_line1                VARCHAR(255),
    address_line2                VARCHAR(255),
    city                         VARCHAR(100),
    state                        VARCHAR(50),
    zip                          VARCHAR(20),
    country                      VARCHAR(100),
    phone1                       VARCHAR(20),
    phone2                       VARCHAR(20),
    government_id                VARCHAR(50),
    electronic_funds_account_ref VARCHAR(64),
    primary_card_holder_indicator CHAR(1),
    created_at                   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                   VARCHAR(64)   NOT NULL DEFAULT 'SYSTEM',
    updated_at                   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by                   VARCHAR(64)   NOT NULL DEFAULT 'SYSTEM',
    version                      BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_customers PRIMARY KEY (customer_id),
    CONSTRAINT chk_customers_credit_score CHECK (credit_score IS NULL OR (credit_score >= 0 AND credit_score <= 999)),
    CONSTRAINT chk_customers_primary_holder CHECK (primary_card_holder_indicator IS NULL OR primary_card_holder_indicator IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_customers_last_name ON customers (last_name, first_name);
CREATE INDEX IF NOT EXISTS idx_customers_ssn       ON customers (ssn);
CREATE INDEX IF NOT EXISTS idx_customers_phone1    ON customers (phone1);

-- ---------------------------------------------------------------
-- TABLE: accounts
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS accounts (
    account_id            VARCHAR(11)    NOT NULL,
    customer_id           VARCHAR(32),
    account_status        VARCHAR(30)    NOT NULL,
    current_balance       DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    credit_limit          DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    cash_credit_limit     DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    current_cycle_credit  DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    current_cycle_debit   DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    open_date             DATE,
    expiration_date       DATE,
    reissue_date          DATE,
    group_id              VARCHAR(64),
    created_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    updated_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by            VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    version               BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_accounts PRIMARY KEY (account_id),
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
);

CREATE INDEX IF NOT EXISTS idx_accounts_customer   ON accounts (customer_id);
CREATE INDEX IF NOT EXISTS idx_accounts_status     ON accounts (account_status);

-- ---------------------------------------------------------------
-- TABLE: cards
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cards (
    card_number   VARCHAR(16)  NOT NULL,
    account_id    VARCHAR(11),
    card_name     VARCHAR(100),
    expiry_month  INTEGER,
    expiry_year   INTEGER,
    card_status   VARCHAR(30),
    active_status CHAR(1),
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    version       BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_cards PRIMARY KEY (card_number),
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts (account_id),
    CONSTRAINT chk_cards_active_status CHECK (active_status IS NULL OR active_status IN ('Y', 'N')),
    CONSTRAINT chk_cards_expiry_month CHECK (expiry_month IS NULL OR (expiry_month >= 1 AND expiry_month <= 12))
);

CREATE INDEX IF NOT EXISTS idx_cards_account ON cards (account_id);
CREATE INDEX IF NOT EXISTS idx_cards_status  ON cards (card_status);

-- ---------------------------------------------------------------
-- TABLE: account_card_links
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS account_card_links (
    link_id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    account_id VARCHAR(11)  NOT NULL,
    card_number VARCHAR(16) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(64) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_account_card_links PRIMARY KEY (link_id),
    CONSTRAINT fk_acl_account FOREIGN KEY (account_id)  REFERENCES accounts (account_id),
    CONSTRAINT fk_acl_card    FOREIGN KEY (card_number) REFERENCES cards (card_number),
    CONSTRAINT uq_acl_account_card UNIQUE (account_id, card_number)
);

CREATE INDEX IF NOT EXISTS idx_acl_account ON account_card_links (account_id);
CREATE INDEX IF NOT EXISTS idx_acl_card    ON account_card_links (card_number);

-- ---------------------------------------------------------------
-- TABLE: transaction_types
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transaction_types (
    type_code   VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_transaction_types PRIMARY KEY (type_code)
);

-- ---------------------------------------------------------------
-- TABLE: transactions
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id       VARCHAR(64)    NOT NULL,
    account_id           VARCHAR(11),
    card_number          VARCHAR(16),
    transaction_type     VARCHAR(50),
    category_type        VARCHAR(50),
    source               VARCHAR(50),
    amount               DECIMAL(18, 2) NOT NULL,
    description          VARCHAR(255),
    original_timestamp   TIMESTAMP,
    processed_timestamp  TIMESTAMP,
    merchant_id          VARCHAR(64),
    merchant_name        VARCHAR(255),
    merchant_city        VARCHAR(100),
    merchant_zip         VARCHAR(20),
    created_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    updated_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by           VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    version              BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_transactions PRIMARY KEY (transaction_id),
    CONSTRAINT fk_txn_account FOREIGN KEY (account_id) REFERENCES accounts (account_id)
);

CREATE INDEX IF NOT EXISTS idx_txn_account     ON transactions (account_id);
CREATE INDEX IF NOT EXISTS idx_txn_card        ON transactions (card_number);
CREATE INDEX IF NOT EXISTS idx_txn_type        ON transactions (transaction_type);
CREATE INDEX IF NOT EXISTS idx_txn_orig_ts     ON transactions (original_timestamp);

-- ---------------------------------------------------------------
-- TABLE: pending_auth_summaries
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pending_auth_summaries (
    account_id   VARCHAR(11)    NOT NULL,
    count        INTEGER        NOT NULL DEFAULT 0,
    total_amount DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by   VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    version      BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_pending_auth_summaries PRIMARY KEY (account_id),
    CONSTRAINT fk_pas_account FOREIGN KEY (account_id) REFERENCES accounts (account_id)
);

-- ---------------------------------------------------------------
-- TABLE: pending_auth_details
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pending_auth_details (
    authorization_id    VARCHAR(64)    NOT NULL,
    account_id          VARCHAR(11),
    card_number         VARCHAR(16),
    amount              DECIMAL(18, 2) NOT NULL,
    merchant_id         VARCHAR(64),
    merchant_name       VARCHAR(255),
    merchant_city       VARCHAR(100),
    merchant_zip        VARCHAR(20),
    status              VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    fraud_flag          CHAR(1)        NOT NULL DEFAULT 'N',
    request_timestamp   TIMESTAMP,
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    updated_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(64)    NOT NULL DEFAULT 'SYSTEM',
    version             BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_pending_auth_details PRIMARY KEY (authorization_id),
    CONSTRAINT fk_pad_account FOREIGN KEY (account_id) REFERENCES accounts (account_id),
    CONSTRAINT chk_pad_fraud_flag CHECK (fraud_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_pad_account ON pending_auth_details (account_id);
CREATE INDEX IF NOT EXISTS idx_pad_status  ON pending_auth_details (status);

-- ---------------------------------------------------------------
-- TABLE: fraud_records
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fraud_records (
    fraud_id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    authorization_id  VARCHAR(64)  NOT NULL,
    action            VARCHAR(10)  NOT NULL,
    fraud_flag        CHAR(1)      NOT NULL,
    notes             VARCHAR(500),
    actioned_by       VARCHAR(64),
    actioned_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_fraud_records PRIMARY KEY (fraud_id),
    CONSTRAINT fk_fr_auth FOREIGN KEY (authorization_id) REFERENCES pending_auth_details (authorization_id),
    CONSTRAINT chk_fr_action CHECK (action IN ('MARK', 'UNMARK')),
    CONSTRAINT chk_fr_fraud_flag CHECK (fraud_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fr_auth ON fraud_records (authorization_id);

-- ---------------------------------------------------------------
-- TABLE: report_requests
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS report_requests (
    request_id    VARCHAR(64)  NOT NULL,
    report_type   VARCHAR(20)  NOT NULL,
    start_date    DATE,
    end_date      DATE,
    status        VARCHAR(20)  NOT NULL DEFAULT 'SUBMITTED',
    submitted_by  VARCHAR(64),
    submitted_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    version       BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_report_requests PRIMARY KEY (request_id),
    CONSTRAINT chk_rr_type CHECK (report_type IN ('MONTHLY', 'YEARLY', 'CUSTOM')),
    CONSTRAINT chk_rr_status CHECK (status IN ('SUBMITTED', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_rr_type   ON report_requests (report_type);
CREATE INDEX IF NOT EXISTS idx_rr_status ON report_requests (status);

-- ---------------------------------------------------------------
-- TABLE: idempotency_keys
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS idempotency_keys (
    idempotency_key   VARCHAR(128)  NOT NULL,
    operation         VARCHAR(64)   NOT NULL,
    response_payload  CLOB,
    http_status       INTEGER,
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at        TIMESTAMP,
    CONSTRAINT pk_idempotency_keys PRIMARY KEY (idempotency_key)
);

-- ---------------------------------------------------------------
-- TABLE: job_runs
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS job_runs (
    job_run_id     VARCHAR(64)  NOT NULL,
    job_name       VARCHAR(100) NOT NULL,
    run_mode       VARCHAR(20)  NOT NULL DEFAULT 'ON_DEMAND',
    status         VARCHAR(20)  NOT NULL DEFAULT 'ACCEPTED',
    triggered_by   VARCHAR(64),
    parameters     VARCHAR(1000),
    started_at     TIMESTAMP,
    completed_at   TIMESTAMP,
    error_message  VARCHAR(2000),
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(64)  NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_job_runs PRIMARY KEY (job_run_id),
    CONSTRAINT chk_jr_status CHECK (status IN ('ACCEPTED', 'RUNNING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_jr_job_name ON job_runs (job_name);
CREATE INDEX IF NOT EXISTS idx_jr_status   ON job_runs (status);
