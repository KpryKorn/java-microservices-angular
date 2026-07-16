-- Jeu de données de test pour le PoC.
-- Les keycloak_user_id ci-dessous sont des valeurs arbitraires : Keycloak génère ses propres
-- UUID à l'import du realm (non prévisibles), elles ne correspondent donc pas aux comptes réels
-- "user"/"admin" mais permettent de démontrer le schéma et ses relations.
INSERT INTO user_profile (id, keycloak_user_id, first_name, last_name, birth_date, phone, address_line, city, postal_code, country, account_active, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', '21111111-1111-1111-1111-111111111111', 'John', 'Doe', '1990-04-12', '+33612345678', '12 rue des Lilas', 'Paris', '75011', 'France', TRUE, now()),
    ('22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Admin', 'Sénior', '1985-01-01', '+33698765432', '5 avenue de la République', 'Lyon', '69001', 'France', TRUE, now());

INSERT INTO document (id, user_id, type, s3_key, status, uploaded_at)
VALUES
    ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'IDENTITY_CARD', 'user-docs/john-doe/cni.pdf', 'VALIDATED', now()),
    ('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'PROOF_OF_ADDRESS', 'user-docs/john-doe/justificatif.pdf', 'PENDING', now());

INSERT INTO communication_pref (user_id, newsletter, sms, push)
VALUES
    ('11111111-1111-1111-1111-111111111111', TRUE, FALSE, TRUE),
    ('22222222-2222-2222-2222-222222222222', FALSE, TRUE, TRUE);
