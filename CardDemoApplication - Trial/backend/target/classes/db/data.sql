-- =============================================================
-- CardDemo Application — Comprehensive Test Data
-- Covers every operation for both ADMIN and STANDARD roles.
--
-- ADMIN users  : USR001 (John Doe)   / USR004 (Diana Prince)
-- STANDARD users: USR002 (Mary Smith) / USR003 (Alice Brown)
--                 USR005 (Bob Taylor) / USR006 (Carol White)
--
-- Passwords:
--   Admin@123  → USR001, USR004
--   User@123   → USR002, USR003, USR005, USR006
-- =============================================================

-- ---------------------------------------------------------------
-- USERS
--   BCrypt hashes verified against seed passwords.
--   Admin@123  : $2a$10$ERaPa2ob7PPkEgWhCWyCy.Q4fS5QBTzSJNj5kzVAFTaOtaPV0HkrS
--   User@123   : $2a$10$bolL/zeA4epj1R688G2iCuCxUk2WdwBiGLfP8xsRI8JLXmRBaHdh.
-- ---------------------------------------------------------------
MERGE INTO users (user_id, first_name, last_name, password_hash, user_type, status, created_by, updated_by, version)
KEY (user_id)
VALUES
-- ADMIN accounts
('USR001', 'John',   'Doe',     '$2a$10$ERaPa2ob7PPkEgWhCWyCy.Q4fS5QBTzSJNj5kzVAFTaOtaPV0HkrS', 'ADMIN',    'ACTIVE', 'SYSTEM', 'SYSTEM', 0),
('USR004', 'Diana',  'Prince',  '$2a$10$ERaPa2ob7PPkEgWhCWyCy.Q4fS5QBTzSJNj5kzVAFTaOtaPV0HkrS', 'ADMIN',    'ACTIVE', 'SYSTEM', 'SYSTEM', 0),
-- STANDARD accounts
('USR002', 'Mary',   'Smith',   '$2a$10$bolL/zeA4epj1R688G2iCuCxUk2WdwBiGLfP8xsRI8JLXmRBaHdh.', 'STANDARD', 'ACTIVE', 'SYSTEM', 'SYSTEM', 0),
('USR003', 'Alice',  'Brown',   '$2a$10$bolL/zeA4epj1R688G2iCuCxUk2WdwBiGLfP8xsRI8JLXmRBaHdh.', 'STANDARD', 'ACTIVE', 'SYSTEM', 'SYSTEM', 0),
('USR005', 'Bob',    'Taylor',  '$2a$10$bolL/zeA4epj1R688G2iCuCxUk2WdwBiGLfP8xsRI8JLXmRBaHdh.', 'STANDARD', 'ACTIVE', 'SYSTEM', 'SYSTEM', 0),
('USR006', 'Carol',  'White',   '$2a$10$bolL/zeA4epj1R688G2iCuCxUk2WdwBiGLfP8xsRI8JLXmRBaHdh.', 'STANDARD', 'ACTIVE', 'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- CUSTOMERS
--   6 customers — one per account below.
-- ---------------------------------------------------------------
MERGE INTO customers (customer_id, first_name, middle_name, last_name,
                       ssn, credit_score, date_of_birth,
                       address_line1, address_line2, city, state, zip, country,
                       phone1, phone2, government_id,
                       electronic_funds_account_ref, primary_card_holder_indicator,
                       created_by, updated_by, version)
KEY (customer_id)
VALUES
-- Customer for account 12345678901 (has balance → bill payment possible)
('CUST001', 'Jane',    'A', 'Doe',     '123-45-6789', 710, '1990-05-20',
 '100 Main St',      NULL,     'Austin',  'TX', '78701', 'US', '5125551001', '5125551002',
 'DL12345678', 'EFA001', 'Y', 'SYSTEM', 'SYSTEM', 0),

-- Customer for account 23456789012 (has balance → bill payment possible)
('CUST002', 'Robert',  'B', 'Wilson',  '456-78-9012', 680, '1985-11-15',
 '200 Oak Ave',      'Apt 5',  'Dallas',  'TX', '75201', 'US', '2145552002', '2145553003',
 'DL87654321', 'EFA002', 'Y', 'SYSTEM', 'SYSTEM', 0),

-- Customer for account 34567890123 (zero balance → bill payment NOT possible)
('CUST003', 'Susan',   'C', 'Lee',     '789-01-2345', 730, '1978-03-08',
 '300 Elm Blvd',     NULL,     'Houston', 'TX', '77001', 'US', '7135554004', '',
 'DL11223344', 'EFA003', 'Y', 'SYSTEM', 'SYSTEM', 0),

-- Customer for account 45678901234 (high balance → good for bill payment + fraud tests)
('CUST004', 'Michael', 'D', 'Johnson', '321-54-9870', 760, '1982-07-22',
 '400 Pine Road',    'Suite 1', 'San Antonio', 'TX', '78201', 'US', '2105556001', '',
 'DL55667788', 'EFA004', 'Y', 'SYSTEM', 'SYSTEM', 0),

-- Customer for account 56789012345 (SUSPENDED status → edge case)
('CUST005', 'Emily',   'E', 'Davis',   '654-32-1098', 540, '1995-12-01',
 '500 Cedar Lane',   NULL,     'Austin',  'TX', '78702', 'US', '5125557001', '5125557002',
 'DL99887766', 'EFA005', 'N', 'SYSTEM', 'SYSTEM', 0),

-- Customer for account 67890123456 (no transactions yet → copy-last test shows empty)
('CUST006', 'James',   'F', 'Martinez','987-65-4321', 800, '1970-09-15',
 '600 Birch Court',  NULL,     'Dallas',  'TX', '75202', 'US', '2145558001', '',
 'DL33445566', 'EFA006', 'Y', 'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- ACCOUNTS
--   Varied statuses and balances to exercise every path.
-- ---------------------------------------------------------------
MERGE INTO accounts (account_id, customer_id, account_status,
                      current_balance, credit_limit, cash_credit_limit,
                      current_cycle_credit, current_cycle_debit,
                      open_date, expiration_date, reissue_date, group_id,
                      created_by, updated_by, version)
KEY (account_id)
VALUES
-- ACTIVE, positive balance → bill payment valid
('12345678901', 'CUST001', 'ACTIVE',  450.75,  5000.00, 1000.00,  100.00,  50.00,
 '2025-01-01', '2028-01-01', '2027-01-01', 'GRP001', 'SYSTEM', 'SYSTEM', 0),

-- ACTIVE, high balance → bill payment valid; multiple transactions
('23456789012', 'CUST002', 'ACTIVE', 1200.50,  8000.00, 2000.00,  300.00, 150.00,
 '2024-06-15', '2027-06-15', '2026-06-15', 'GRP001', 'SYSTEM', 'SYSTEM', 0),

-- ACTIVE, zero balance → bill payment NOT valid (nothing to pay)
('34567890123', 'CUST003', 'ACTIVE',    0.00,  3000.00,  500.00,    0.00,   0.00,
 '2025-03-01', '2028-03-01', '2027-03-01', 'GRP002', 'SYSTEM', 'SYSTEM', 0),

-- ACTIVE, very high balance → authorization decision declines (over-limit tests)
('45678901234', 'CUST004', 'ACTIVE', 4800.00,  5000.00, 1000.00,  900.00, 800.00,
 '2023-11-01', '2026-11-01', '2025-11-01', 'GRP001', 'SYSTEM', 'SYSTEM', 0),

-- SUSPENDED → account update flow (status change test)
('56789012345', 'CUST005', 'SUSPENDED', 250.00, 2000.00,  400.00,   50.00,  25.00,
 '2024-02-01', '2027-02-01', '2026-02-01', 'GRP003', 'SYSTEM', 'SYSTEM', 0),

-- ACTIVE, zero balance, no transactions → tests empty transaction list
('67890123456', 'CUST006', 'ACTIVE',    0.00, 10000.00, 2500.00,    0.00,   0.00,
 '2022-05-10', '2029-05-10', '2028-05-10', 'GRP002', 'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- CARDS
--   One primary card per account + one expired card for edge cases.
-- ---------------------------------------------------------------
MERGE INTO cards (card_number, account_id, card_name, expiry_month, expiry_year,
                   card_status, active_status, created_by, updated_by, version)
KEY (card_number)
VALUES
-- Primary cards (active)
('4444333322221111', '12345678901', 'JANE DOE',          12, 2028, 'ACTIVE',   'Y', 'SYSTEM', 'SYSTEM', 0),
('5555444433332222', '23456789012', 'ROBERT WILSON',      6, 2027, 'ACTIVE',   'Y', 'SYSTEM', 'SYSTEM', 0),
('3333222211110000', '34567890123', 'SUSAN LEE',           3, 2028, 'ACTIVE',   'Y', 'SYSTEM', 'SYSTEM', 0),
('6666555544443333', '45678901234', 'MICHAEL JOHNSON',     9, 2026, 'ACTIVE',   'Y', 'SYSTEM', 'SYSTEM', 0),
('7777666655554444', '56789012345', 'EMILY DAVIS',         1, 2027, 'INACTIVE', 'N', 'SYSTEM', 'SYSTEM', 0),
('8888777766665555', '67890123456', 'JAMES MARTINEZ',     12, 2029, 'ACTIVE',   'Y', 'SYSTEM', 'SYSTEM', 0),
-- Secondary card on account 12345678901 (for multi-card search test)
('9999888877776666', '12345678901', 'JANE DOE SECONDARY',  8, 2027, 'ACTIVE',   'Y', 'SYSTEM', 'SYSTEM', 0),
-- Expired card on account 23456789012 (still viewable per LLD)
('1111000099998888', '23456789012', 'ROBERT WILSON OLD',   1, 2023, 'EXPIRED',  'N', 'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- ACCOUNT-CARD LINKS
-- ---------------------------------------------------------------
MERGE INTO account_card_links (account_id, card_number, created_by)
KEY (account_id, card_number)
VALUES
('12345678901', '4444333322221111', 'SYSTEM'),
('12345678901', '9999888877776666', 'SYSTEM'),
('23456789012', '5555444433332222', 'SYSTEM'),
('23456789012', '1111000099998888', 'SYSTEM'),
('34567890123', '3333222211110000', 'SYSTEM'),
('45678901234', '6666555544443333', 'SYSTEM'),
('56789012345', '7777666655554444', 'SYSTEM'),
('67890123456', '8888777766665555', 'SYSTEM');

-- ---------------------------------------------------------------
-- TRANSACTION TYPES (Reference Data)
--   Covers admin add/update/delete maintenance operations.
-- ---------------------------------------------------------------
MERGE INTO transaction_types (type_code, description, created_by, updated_by, version)
KEY (type_code)
VALUES
('PURCHASE',   'Retail Purchase',          'SYSTEM', 'SYSTEM', 0),
('PAYMENT',    'Bill Payment',             'SYSTEM', 'SYSTEM', 0),
('REFUND',     'Merchant Refund',          'SYSTEM', 'SYSTEM', 0),
('CASH_ADV',   'Cash Advance',             'SYSTEM', 'SYSTEM', 0),
('INTEREST',   'Interest Charge',          'SYSTEM', 'SYSTEM', 0),
('FEE',        'Service Fee',              'SYSTEM', 'SYSTEM', 0),
('TRANSFER',   'Balance Transfer',         'SYSTEM', 'SYSTEM', 0),
('REVERSAL',   'Transaction Reversal',     'SYSTEM', 'SYSTEM', 0),
('ADJUSTMENT', 'Account Adjustment',       'SYSTEM', 'SYSTEM', 0),
-- Extra type to demonstrate delete (not referenced by any transaction)
('PROMO',      'Promotional Credit',       'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- TRANSACTIONS
--   Rich variety: multiple accounts, types, categories, merchants.
--   Covers: list pagination, detail view, copy-last, add-transaction.
-- ---------------------------------------------------------------
MERGE INTO transactions (transaction_id, account_id, card_number, transaction_type,
                          category_type, source, amount, description,
                          original_timestamp, processed_timestamp,
                          merchant_id, merchant_name, merchant_city, merchant_zip,
                          created_by, updated_by, version)
KEY (transaction_id)
VALUES
-- ── Account 12345678901 / Card 4444333322221111 ──────────────────
('TXN1001', '12345678901', '4444333322221111', 'PURCHASE', 'RETAIL',   'ONLINE',
 100.50, 'Online purchase at Tech Store',
 PARSEDATETIME('2026-07-01 10:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-01 10:01:00','yyyy-MM-dd HH:mm:ss'),
 'M001', 'TECH STORE', 'Austin', '78701', 'SYSTEM', 'SYSTEM', 0),

('TXN1002', '12345678901', '4444333322221111', 'PURCHASE', 'GROCERY',  'SWIPE',
 55.25, 'Grocery Store Purchase',
 PARSEDATETIME('2026-07-05 14:30:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-05 14:31:00','yyyy-MM-dd HH:mm:ss'),
 'M002', 'SUPER GROCERY', 'Austin', '78702', 'SYSTEM', 'SYSTEM', 0),

('TXN1003', '12345678901', '4444333322221111', 'PURCHASE', 'DINING',   'SWIPE',
 45.00, 'Restaurant Dinner',
 PARSEDATETIME('2026-07-10 19:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-10 19:01:00','yyyy-MM-dd HH:mm:ss'),
 'M003', 'FINE DINING', 'Austin', '78703', 'SYSTEM', 'SYSTEM', 0),

('TXN1004', '12345678901', '4444333322221111', 'PURCHASE', 'FUEL',     'SWIPE',
 62.00, 'Fuel at Shell Station',
 PARSEDATETIME('2026-07-12 08:15:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-12 08:16:00','yyyy-MM-dd HH:mm:ss'),
 'M010', 'SHELL STATION', 'Austin', '78704', 'SYSTEM', 'SYSTEM', 0),

('TXN1005', '12345678901', '4444333322221111', 'REFUND',   'RETAIL',   'ONLINE',
 30.00, 'Refund — returned item',
 PARSEDATETIME('2026-07-15 11:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-15 11:05:00','yyyy-MM-dd HH:mm:ss'),
 'M001', 'TECH STORE', 'Austin', '78701', 'SYSTEM', 'SYSTEM', 0),

('TXN1006', '12345678901', '9999888877776666', 'PURCHASE', 'ENTERTAINMENT', 'CONTACTLESS',
 89.99, 'Concert ticket purchase',
 PARSEDATETIME('2026-07-20 17:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-20 17:01:00','yyyy-MM-dd HH:mm:ss'),
 'M011', 'TICKET MASTER', 'Austin', '78705', 'SYSTEM', 'SYSTEM', 0),

('TXN1007', '12345678901', '4444333322221111', 'FEE',      'SERVICE',  'SYSTEM',
 5.00, 'Monthly maintenance fee',
 PARSEDATETIME('2026-08-01 00:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-01 00:00:00','yyyy-MM-dd HH:mm:ss'),
 NULL, NULL, NULL, NULL, 'SYSTEM', 'SYSTEM', 0),

('TXN1008', '12345678901', '4444333322221111', 'INTEREST', 'FINANCE',  'SYSTEM',
 12.50, 'Monthly interest charge',
 PARSEDATETIME('2026-08-01 00:01:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-01 00:01:00','yyyy-MM-dd HH:mm:ss'),
 NULL, NULL, NULL, NULL, 'SYSTEM', 'SYSTEM', 0),

-- ── Account 23456789012 / Card 5555444433332222 ──────────────────
('TXN2001', '23456789012', '5555444433332222', 'PURCHASE', 'TRAVEL',   'ONLINE',
 500.00, 'Flight booking — Dallas to NYC',
 PARSEDATETIME('2026-07-15 08:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-15 08:01:00','yyyy-MM-dd HH:mm:ss'),
 'M004', 'AIR TRAVEL CO', 'Dallas', '75201', 'SYSTEM', 'SYSTEM', 0),

('TXN2002', '23456789012', '5555444433332222', 'PAYMENT',  'PAYMENT',  'MANUAL',
 200.00, 'Bill payment',
 PARSEDATETIME('2026-07-20 12:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-20 12:01:00','yyyy-MM-dd HH:mm:ss'),
 NULL, NULL, NULL, NULL, 'SYSTEM', 'SYSTEM', 0),

('TXN2003', '23456789012', '5555444433332222', 'PURCHASE', 'HOTEL',    'ONLINE',
 350.00, 'Hotel booking — Marriott Dallas',
 PARSEDATETIME('2026-07-22 09:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-22 09:01:00','yyyy-MM-dd HH:mm:ss'),
 'M005', 'MARRIOTT HOTELS', 'Dallas', '75202', 'SYSTEM', 'SYSTEM', 0),

('TXN2004', '23456789012', '5555444433332222', 'PURCHASE', 'RETAIL',   'SWIPE',
 75.60, 'Department store purchase',
 PARSEDATETIME('2026-07-28 15:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-28 15:01:00','yyyy-MM-dd HH:mm:ss'),
 'M006', 'NORDSTROM', 'Dallas', '75203', 'SYSTEM', 'SYSTEM', 0),

('TXN2005', '23456789012', '5555444433332222', 'CASH_ADV', 'CASH',     'ATM',
 100.00, 'ATM Cash Withdrawal',
 PARSEDATETIME('2026-08-01 10:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-01 10:01:00','yyyy-MM-dd HH:mm:ss'),
 'ATM001', 'CHASE ATM', 'Dallas', '75204', 'SYSTEM', 'SYSTEM', 0),

('TXN2006', '23456789012', '1111000099998888', 'PURCHASE', 'GROCERY',  'SWIPE',
 44.90, 'Old card — grocery before expiry',
 PARSEDATETIME('2023-01-10 11:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2023-01-10 11:01:00','yyyy-MM-dd HH:mm:ss'),
 'M002', 'KROGER', 'Dallas', '75205', 'SYSTEM', 'SYSTEM', 0),

-- ── Account 34567890123 / Card 3333222211110000 (zero balance) ───
('TXN3001', '34567890123', '3333222211110000', 'PURCHASE', 'RETAIL',   'ONLINE',
 120.00, 'Amazon purchase',
 PARSEDATETIME('2026-06-10 13:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-06-10 13:02:00','yyyy-MM-dd HH:mm:ss'),
 'M007', 'AMAZON', 'Houston', '77001', 'SYSTEM', 'SYSTEM', 0),

('TXN3002', '34567890123', '3333222211110000', 'PAYMENT',  'PAYMENT',  'MANUAL',
 120.00, 'Full payment',
 PARSEDATETIME('2026-06-15 09:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-06-15 09:01:00','yyyy-MM-dd HH:mm:ss'),
 NULL, NULL, NULL, NULL, 'SYSTEM', 'SYSTEM', 0),

-- ── Account 45678901234 / Card 6666555544443333 (near limit) ─────
('TXN4001', '45678901234', '6666555544443333', 'PURCHASE', 'TRAVEL',   'ONLINE',
 2000.00, 'International flight booking',
 PARSEDATETIME('2026-07-05 07:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-05 07:01:00','yyyy-MM-dd HH:mm:ss'),
 'M008', 'DELTA AIRLINES', 'San Antonio', '78201', 'SYSTEM', 'SYSTEM', 0),

('TXN4002', '45678901234', '6666555544443333', 'PURCHASE', 'HOTEL',    'ONLINE',
 1500.00, 'Hotel — 5 nights',
 PARSEDATETIME('2026-07-10 12:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-10 12:01:00','yyyy-MM-dd HH:mm:ss'),
 'M009', 'HILTON HOTELS', 'San Antonio', '78202', 'SYSTEM', 'SYSTEM', 0),

('TXN4003', '45678901234', '6666555544443333', 'PURCHASE', 'DINING',   'SWIPE',
 85.50, 'Business dinner',
 PARSEDATETIME('2026-08-02 20:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-02 20:01:00','yyyy-MM-dd HH:mm:ss'),
 'M003', 'STEAKHOUSE', 'San Antonio', '78203', 'SYSTEM', 'SYSTEM', 0),

-- ── Account 56789012345 / Card 7777666655554444 (SUSPENDED) ──────
('TXN5001', '56789012345', '7777666655554444', 'PURCHASE', 'RETAIL',   'SWIPE',
 250.00, 'Electronics purchase',
 PARSEDATETIME('2026-05-01 15:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-05-01 15:01:00','yyyy-MM-dd HH:mm:ss'),
 'M012', 'BEST BUY', 'Austin', '78702', 'SYSTEM', 'SYSTEM', 0);

-- Account 67890123456 intentionally has NO transactions (copy-last returns 404).

-- ---------------------------------------------------------------
-- PENDING AUTHORIZATION SUMMARIES
-- ---------------------------------------------------------------
MERGE INTO pending_auth_summaries (account_id, count, total_amount, updated_by, version)
KEY (account_id)
VALUES
-- 3 pending auths on account 12345678901
('12345678901', 3, 275.24, 'SYSTEM', 0),
-- 2 pending auths on account 23456789012
('23456789012', 2, 199.98, 'SYSTEM', 0),
-- 1 pending auth on account 45678901234 (already marked fraud)
('45678901234', 1,  89.50, 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- PENDING AUTHORIZATION DETAILS
--   Covers: view detail, next-nav, mark-fraud, unmark-fraud.
-- ---------------------------------------------------------------
MERGE INTO pending_auth_details (authorization_id, account_id, card_number, amount,
                                  merchant_id, merchant_name, merchant_city, merchant_zip,
                                  status, fraud_flag, request_timestamp,
                                  created_by, updated_by, version)
KEY (authorization_id)
VALUES
-- ── Account 12345678901 ──────────────────────────────────────────
-- AUTH001 → normal pending, not fraud (mark-fraud test target)
('AUTH001', '12345678901', '4444333322221111', 100.50,
 'M001', 'ABC STORE',       'Austin', '78701', 'PENDING', 'N',
 PARSEDATETIME('2026-08-05 09:00:00','yyyy-MM-dd HH:mm:ss'),
 'SYSTEM', 'SYSTEM', 0),

-- AUTH002 → normal pending, not fraud (next-item navigation)
('AUTH002', '12345678901', '4444333322221111',  50.25,
 'M002', 'GAS STATION',     'Austin', '78702', 'PENDING', 'N',
 PARSEDATETIME('2026-08-05 10:30:00','yyyy-MM-dd HH:mm:ss'),
 'SYSTEM', 'SYSTEM', 0),

-- AUTH003 → already marked as FRAUD (unmark-fraud test target)
('AUTH003', '12345678901', '9999888877776666', 124.49,
 'M013', 'SUSPICIOUS SHOP', 'Austin', '78710', 'PENDING', 'Y',
 PARSEDATETIME('2026-08-05 11:45:00','yyyy-MM-dd HH:mm:ss'),
 'SYSTEM', 'SYSTEM', 0),

-- ── Account 23456789012 ──────────────────────────────────────────
-- AUTH004 → normal pending
('AUTH004', '23456789012', '5555444433332222',  99.99,
 'M005', 'ONLINE SHOP',    'Dallas', '75201', 'PENDING', 'N',
 PARSEDATETIME('2026-08-05 11:00:00','yyyy-MM-dd HH:mm:ss'),
 'SYSTEM', 'SYSTEM', 0),

-- AUTH005 → normal pending (second item for next-nav on this account)
('AUTH005', '23456789012', '5555444433332222',  99.99,
 'M006', 'FASHION HUB',    'Dallas', '75205', 'PENDING', 'N',
 PARSEDATETIME('2026-08-05 13:00:00','yyyy-MM-dd HH:mm:ss'),
 'SYSTEM', 'SYSTEM', 0),

-- ── Account 45678901234 ──────────────────────────────────────────
-- AUTH006 → already marked fraud (demonstrates existing fraud state)
('AUTH006', '45678901234', '6666555544443333',  89.50,
 'M014', 'FRAUD MERCHANT',  'San Antonio', '78210', 'PENDING', 'Y',
 PARSEDATETIME('2026-08-04 14:00:00','yyyy-MM-dd HH:mm:ss'),
 'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- FRAUD RECORDS
--   Pre-existing fraud history for AUTH003 and AUTH006.
--   fraud_id is an identity column — use INSERT WHERE NOT EXISTS.
-- ---------------------------------------------------------------
INSERT INTO fraud_records (authorization_id, action, fraud_flag, notes, actioned_by, actioned_at, created_by)
SELECT 'AUTH003', 'MARK', 'Y', 'Suspicious merchant reported by customer',
       'USR001', PARSEDATETIME('2026-08-05 12:00:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1 FROM fraud_records
    WHERE authorization_id = 'AUTH003'
      AND action           = 'MARK'
      AND actioned_at      = PARSEDATETIME('2026-08-05 12:00:00','yyyy-MM-dd HH:mm:ss')
);

INSERT INTO fraud_records (authorization_id, action, fraud_flag, notes, actioned_by, actioned_at, created_by)
SELECT 'AUTH006', 'MARK', 'Y', 'Card not present — flagged by automated system',
       'USR004', PARSEDATETIME('2026-08-04 14:30:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1 FROM fraud_records
    WHERE authorization_id = 'AUTH006'
      AND action           = 'MARK'
      AND actioned_at      = PARSEDATETIME('2026-08-04 14:30:00','yyyy-MM-dd HH:mm:ss')
);

-- ---------------------------------------------------------------
-- REPORT REQUESTS
--   Pre-existing requests to test GET /reports/requests/{id}.
-- ---------------------------------------------------------------
MERGE INTO report_requests (request_id, report_type, start_date, end_date,
                              status, submitted_by, submitted_at,
                              created_by, updated_by, version)
KEY (request_id)
VALUES
-- SUBMITTED monthly report (USR002 — standard user)
('RPT00000001', 'MONTHLY',  '2026-07-01', '2026-07-31', 'SUBMITTED',
 'USR002', PARSEDATETIME('2026-08-01 09:00:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM', 'SYSTEM', 0),

-- COMPLETED yearly report (USR001 — admin user)
('RPT00000002', 'YEARLY',   '2026-01-01', '2026-12-31', 'COMPLETED',
 'USR001', PARSEDATETIME('2026-01-02 08:00:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM', 'SYSTEM', 0),

-- SUBMITTED custom date range (USR003 — standard user)
('RPT00000003', 'CUSTOM',   '2026-06-01', '2026-06-30', 'SUBMITTED',
 'USR003', PARSEDATETIME('2026-07-01 10:00:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM', 'SYSTEM', 0),

-- FAILED custom report (demonstrates failure status)
('RPT00000004', 'CUSTOM',   '2025-01-01', '2025-12-31', 'FAILED',
 'USR004', PARSEDATETIME('2026-01-15 11:00:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM', 'SYSTEM', 0),

-- PROCESSING (demonstrates in-progress status)
('RPT00000005', 'MONTHLY',  '2026-08-01', '2026-08-31', 'PROCESSING',
 'USR005', PARSEDATETIME('2026-08-06 07:00:00','yyyy-MM-dd HH:mm:ss'), 'SYSTEM', 'SYSTEM', 0);

-- ---------------------------------------------------------------
-- JOB RUNS
--   Pre-existing job history for GET/test of ops trigger APIs.
-- ---------------------------------------------------------------
MERGE INTO job_runs (job_run_id, job_name, run_mode, status, triggered_by,
                      parameters, started_at, completed_at, error_message, created_by)
KEY (job_run_id)
VALUES
-- Completed daily transaction validation
('JOB00000001', 'daily-transaction-validation', 'SCHEDULED', 'COMPLETED',
 'SYSTEM', '{"businessDate":"2026-08-05"}',
 PARSEDATETIME('2026-08-05 02:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-05 02:05:00','yyyy-MM-dd HH:mm:ss'),
 NULL, 'SYSTEM'),

-- Completed daily transaction posting
('JOB00000002', 'daily-transaction-posting', 'SCHEDULED', 'COMPLETED',
 'SYSTEM', '{"businessDate":"2026-08-05"}',
 PARSEDATETIME('2026-08-05 02:10:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-05 02:18:00','yyyy-MM-dd HH:mm:ss'),
 NULL, 'SYSTEM'),

-- Failed interest processing (demonstrates failure state)
('JOB00000003', 'interest-processing', 'ON_DEMAND', 'FAILED',
 'USR001', '{"businessDate":"2026-08-01"}',
 PARSEDATETIME('2026-08-01 03:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-08-01 03:01:00','yyyy-MM-dd HH:mm:ss'),
 'Rate data source unavailable', 'SYSTEM'),

-- Completed export
('JOB00000004', 'export', 'ON_DEMAND', 'COMPLETED',
 'USR004', '{"businessDate":"2026-07-31"}',
 PARSEDATETIME('2026-07-31 22:00:00','yyyy-MM-dd HH:mm:ss'),
 PARSEDATETIME('2026-07-31 22:30:00','yyyy-MM-dd HH:mm:ss'),
 NULL, 'SYSTEM'),

-- Accepted (just triggered — still running) statement generation
('JOB00000005', 'statements', 'ON_DEMAND', 'ACCEPTED',
 'USR001', '{"businessDate":"2026-08-06"}',
 PARSEDATETIME('2026-08-06 01:00:00','yyyy-MM-dd HH:mm:ss'),
 NULL, NULL, 'SYSTEM');
