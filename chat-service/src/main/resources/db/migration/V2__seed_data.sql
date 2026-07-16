-- Jeu de données de test pour le PoC.
-- Les id ci-dessous sont volontairement alignés avec les keycloak_user_id du seed user-service
-- pour illustrer la cohérence inter-services, même si les vrais UUID Keycloak seront différents.
INSERT INTO user_shadow (id, username, email, synced_at)
VALUES
    ('21111111-1111-1111-1111-111111111111', 'user', 'user@example.com', now()),
    ('22222222-2222-2222-2222-222222222222', 'admin', 'admin@example.com', now());

INSERT INTO ticket (id, user_id, subject, motif, status, created_at, resolved_at)
VALUES
    ('55555555-5555-5555-5555-555555555555', '21111111-1111-1111-1111-111111111111', 'Problème de connexion', 'TECHNIQUE', 'OPEN', now(), NULL),
    ('66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', 'Demande de remboursement', 'FACTURATION', 'RESOLVED', now(), now());

INSERT INTO ticket_message (id, ticket_id, sender_id, content, sent_at)
VALUES
    ('77777777-7777-7777-7777-777777777777', '55555555-5555-5555-5555-555555555555', '21111111-1111-1111-1111-111111111111', 'Bonjour, je n''arrive pas à me connecter depuis ce matin.', now()),
    ('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', 'Bonjour, pouvez-vous préciser le message d''erreur ?', now()),
    ('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', 'Remboursement effectué, merci de votre patience.', now());
