
select * from counterparties;
select * from bank_accounts


--show all tables
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE';

-- Roles
CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE
);

-- Users
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(200),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- User ↔ Roles mapping (many-to-many)
CREATE TABLE user_roles (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, role_id)
);

-- Payment Categories
CREATE TABLE categories (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(150) NOT NULL,
  description TEXT
);

-- Payment Status
CREATE TABLE statuses (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(150) NOT NULL
);

-- Counterparties (vendors, clients, etc.)
CREATE TABLE counterparties (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  details JSONB,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Bank Accounts
CREATE TABLE bank_accounts (
  id BIGSERIAL PRIMARY KEY,
  account_number VARCHAR(64) NOT NULL,
  bank_name VARCHAR(200),
  currency CHAR(3) NOT NULL,
  owner_counterparty_id BIGINT REFERENCES counterparties(id),
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Payments
CREATE TABLE payments (
  id BIGSERIAL PRIMARY KEY,
  direction VARCHAR(10) NOT NULL CHECK (direction IN ('INCOMING','OUTGOING')),
  category_id INTEGER REFERENCES categories(id),
  status_id INTEGER REFERENCES statuses(id),
  amount NUMERIC(18,2) NOT NULL,
  currency CHAR(3) NOT NULL,
  counterparty_id BIGINT REFERENCES counterparties(id),
  bank_account_id BIGINT REFERENCES bank_accounts(id),
  description TEXT,
  reference VARCHAR(200),
  created_by BIGINT REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Audit Trails
CREATE TABLE audit_trails (
  id BIGSERIAL PRIMARY KEY,
  table_name VARCHAR(100) NOT NULL,
  record_id BIGINT,
  action VARCHAR(100) NOT NULL,
  performed_by BIGINT REFERENCES users(id),
  details JSONB,
  performed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Reports Metadata
CREATE TABLE reports (
  id BIGSERIAL PRIMARY KEY,
  report_type VARCHAR(20) NOT NULL, -- MONTHLY/QUARTERLY
  year INTEGER NOT NULL,
  month INTEGER,
  quarter INTEGER,
  file_path TEXT,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  generated_by BIGINT REFERENCES users(id),
  total_incoming NUMERIC(18,2),
  total_outgoing NUMERIC(18,2),
  meta JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);


DROP TABLE IF EXISTS role_requests;

CREATE TABLE role_requests (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    role_name VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' -- PENDING / APPROVED / REJECTED
);

select * from role_requests
truncate role_requests

select * from users
select * from user_roles

---seed data
-- Insert Roles
INSERT INTO roles(name) VALUES
('ADMIN'),
('FINANCE_MANAGER'),
('VIEWER');

-- Insert Payment Statuses
INSERT INTO statuses(code, name) VALUES
('PENDING','Pending'),
('PROCESSING','Processing'),
('COMPLETED','Completed'),
('FAILED','Failed'),
('CANCELLED','Cancelled');

-- Insert Payment Categories
INSERT INTO categories(code, name) VALUES
('SALARY','Salary'),
('VENDOR_PAYMENT','Vendor Payment'),
('CLIENT_INVOICE','Client Invoice');

-- Insert initial Admin user
-- Replace <BCRYPT_HASH> with actual hashed password (e.g., "supersecure")
INSERT INTO users(username, email, password_hash, full_name)
VALUES ('admin', 'admin@example.com', '$2a$10$WUB5gZE0T7ZAm/Pb87JKg.qizpzvlwlbUPXfPT4KHe1ntyvTQ.i/K', 'System Administrator');

-- Assign ADMIN role to the initial admin user
INSERT INTO user_roles(user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username='admin' AND r.name='ADMIN';




INSERT INTO counterparties(name, details)
VALUES ('XYZ Supplies', '{"contact":"xyz@example.com"}');

INSERT INTO bank_accounts(account_number, bank_name, currency, owner_counterparty_id)
VALUES ('1234567890', 'HDFC Bank', 'INR', 1);



