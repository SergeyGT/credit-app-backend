CREATE TABLE IF NOT EXISTS clients (
    id                      BIGSERIAL PRIMARY KEY,
    first_name              VARCHAR(64) NOT NULL,
    last_name               VARCHAR(64) NOT NULL,
    middle_name             VARCHAR(64),
    passport                VARCHAR(10) NOT NULL UNIQUE,
    gender                  VARCHAR(20) NOT NULL,
    family_status           VARCHAR(20) NOT NULL,
    residence_address       VARCHAR(255) NOT NULL,
    registration_address    VARCHAR(255) NOT NULL,
    phone                   VARCHAR(16) NOT NULL,
    employment_start_date   DATE,
    employment_end_date     DATE,
    employment_position     VARCHAR(255),
    organization_name       VARCHAR(255),
    loan_purpose            VARCHAR(255) NOT NULL,

    CONSTRAINT check_employment_dates CHECK (employment_end_date IS NULL OR employment_start_date <= employment_end_date),
    CONSTRAINT check_employment_past CHECK (employment_start_date <= CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS credit_application (
    id                  BIGSERIAL PRIMARY KEY,
    client_id           BIGINT NOT NULL,
    status              VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    requested_money     NUMERIC(12, 2) NOT NULL,
    approved_term       INT,
    approved_money      NUMERIC(12, 2),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE CASCADE,

    CONSTRAINT check_approved_term CHECK (approved_term BETWEEN 1 AND 365),
    CONSTRAINT check_approved_money CHECK (approved_money IS NULL OR approved_money <= requested_money)
);

CREATE TABLE IF NOT EXISTS credit_agreements (
    id                      BIGSERIAL PRIMARY KEY,
    credit_application_id   BIGINT NOT NULL,
    sign_date               DATE,
    sign_status             VARCHAR(16) NOT NULL DEFAULT 'NOT_SIGNED',

    FOREIGN KEY (credit_application_id) REFERENCES credit_application (id) ON DELETE CASCADE
);