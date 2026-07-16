CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Copie locale (shadow) des infos utilisateur Keycloak/user-service : chaque microservice
-- ayant sa propre base, cette table permet de garder une contrainte FK côté chat-service
-- sans dépendance directe à la base user-service. id = id Keycloak de l'utilisateur.
CREATE TABLE user_shadow (
    id          UUID PRIMARY KEY,
    username    VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    synced_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE ticket (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    subject      VARCHAR(255) NOT NULL,
    motif        VARCHAR(100),
    status       VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    resolved_at  TIMESTAMP,
    CONSTRAINT fk_ticket_user_shadow FOREIGN KEY (user_id) REFERENCES user_shadow (id) ON DELETE CASCADE
);

CREATE INDEX idx_ticket_user_id ON ticket (user_id);

CREATE TABLE ticket_message (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID NOT NULL,
    sender_id   UUID NOT NULL,
    content     TEXT NOT NULL,
    sent_at     TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_ticket_message_ticket FOREIGN KEY (ticket_id) REFERENCES ticket (id) ON DELETE CASCADE
);

CREATE INDEX idx_ticket_message_ticket_id ON ticket_message (ticket_id);
