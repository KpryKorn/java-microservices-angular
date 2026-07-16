CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE user_profile (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_user_id  UUID NOT NULL,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    birth_date        DATE,
    phone             VARCHAR(30),
    address_line      VARCHAR(255),
    city              VARCHAR(100),
    postal_code       VARCHAR(20),
    country           VARCHAR(100),
    account_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMP,
    CONSTRAINT uk_user_profile_keycloak_user_id UNIQUE (keycloak_user_id)
);

CREATE TABLE document (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    type         VARCHAR(50) NOT NULL,
    s3_key       VARCHAR(255) NOT NULL,
    status       VARCHAR(30) NOT NULL,
    uploaded_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_document_user_profile FOREIGN KEY (user_id) REFERENCES user_profile (id) ON DELETE CASCADE
);

CREATE INDEX idx_document_user_id ON document (user_id);

CREATE TABLE communication_pref (
    user_id      UUID PRIMARY KEY,
    newsletter   BOOLEAN NOT NULL DEFAULT FALSE,
    sms          BOOLEAN NOT NULL DEFAULT FALSE,
    push         BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_communication_pref_user_profile FOREIGN KEY (user_id) REFERENCES user_profile (id) ON DELETE CASCADE
);
