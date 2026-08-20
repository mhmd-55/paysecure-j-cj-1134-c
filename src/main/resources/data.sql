-- Seed data for CJ-1134-C / Variant C (MySQL)
-- Password for all three seed accounts is: Password123!
-- INSERT IGNORE keeps this safe to re-run every app start (spring.sql.init.mode=always)
-- without erroring on duplicate primary keys.

INSERT IGNORE INTO users (id, username, password, email, user_role, reset_token) VALUES
  (1,    'attacker',     'oQnjaUetVt4dyhzEnw74rJrZp7GqDfQfs8TLc8H/Aeo=', 'attacker@example.com',  'CUSTOMER', NULL),
  (1434, 'testuser1434', 'oQnjaUetVt4dyhzEnw74rJrZp7GqDfQfs8TLc8H/Aeo=', 'test1434@example.com',  'CUSTOMER', NULL),
  (2434, 'admin2434',    'oQnjaUetVt4dyhzEnw74rJrZp7GqDfQfs8TLc8H/Aeo=', 'admin2434@example.com', 'ADMIN',    NULL);

INSERT IGNORE INTO transactions (id, description, amount, account_id) VALUES
  (1, 'Coffee shop purchase', 4.50, 1),
  (2, 'Grocery store',        62.10, 1),
  (3, 'Salary deposit',       1500.00, 1434),
  (4, 'Online subscription',  9.99, 1434);

INSERT IGNORE INTO accounts (id, owner_username, balance) VALUES
  (1,    'attacker',     100.00),
  (1434, 'testuser1434', 1000.00);
