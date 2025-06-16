CREATE DATABASE IF NOT EXISTS insurance_policy;
CREATE DATABASE IF NOT EXISTS insurance_policy_test;


use insurance_policy;
CREATE TABLE coverages (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO coverages (id, name) VALUES
    (UUID(), 'Roubo'),
    (UUID(), 'Perda Total'),
    (UUID(), 'Colisão com Terceiros');


CREATE TABLE assistances (
    id CHAR(36)  PRIMARY KEY,
    description VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO assistances (id, description) VALUES
    (UUID(), 'Guincho até 250km'),
    (UUID(), 'Troca de Óleo'),
    (UUID(), 'Chaveiro 24h');


CREATE TABLE policy_requests (
    id CHAR(36)  PRIMARY KEY,
    customer_id CHAR(36)  NOT NULL,
    product_id CHAR(36) NOT NULL,
    category VARCHAR(50) NOT NULL,
    sales_channel ENUM('MOBILE', 'WHATSAPP', 'WEBSITE') NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'BANK_SLIP', 'PIX') NOT NULL,
    total_monthly_premium_amount DECIMAL(19,2) NOT NULL,
    insured_amount DECIMAL(19,2) NOT NULL,
    status ENUM('RECEIVED','VALIDATED','PENDING', 'APPROVED','REJECTED', 'CANCELLED') NOT NULL,
    created_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP NULL
);

CREATE TABLE policy_request_coverages (
    policy_id CHAR(36)  NOT NULL,
    coverage_id CHAR(36)  NOT NULL,
    coverage_amount DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (policy_id, coverage_id),
    FOREIGN KEY (policy_id) REFERENCES policy_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (coverage_id) REFERENCES coverages(id) ON DELETE RESTRICT
);

CREATE TABLE policy_request_assistances (
    policy_id CHAR(36)  NOT NULL,
    assistance_id CHAR(36)  NOT NULL,
    PRIMARY KEY (policy_id, assistance_id),
    FOREIGN KEY (policy_id) REFERENCES policy_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (assistance_id) REFERENCES assistances(id) ON DELETE RESTRICT
);

CREATE TABLE policy_history (
    policy_id CHAR(36) NOT NULL,
    status ENUM('RECEIVED','VALIDATED','PENDING', 'APPROVED','REJECTED', 'CANCELLED') NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    PRIMARY KEY (policy_id, status, timestamp),
    FOREIGN KEY (policy_id) REFERENCES policy_requests(id) ON DELETE CASCADE
);

