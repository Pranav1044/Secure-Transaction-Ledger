-- Initial test data for Accounts table
-- Account 1: Alice - $1000.00
-- Account 2: Bob - $2000.00
-- Account 3: Charlie - $1500.50
-- Account 4: Diana - $500.00
-- Account 5: Eve - $3000.75

MERGE INTO accounts (id, balance, owner) KEY (id) VALUES
(1, 1000.00, 'Alice'),
(2, 2000.00, 'Bob'),
(3, 1500.50, 'Charlie'),
(4, 500.00, 'Diana'),
(5, 3000.75, 'Eve');