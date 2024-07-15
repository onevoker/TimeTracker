-- Insert roles
INSERT INTO roles (name)
VALUES ('ROLE_User'),
       ('ROLE_Admin');

-- Insert admin:
-- username: onevoker; password: coolAdmin2005
INSERT INTO users (username, password)
VALUES ('onevoker', '$2a$10$k4tgpBtFno3IEVN6YkeMBePacGbs6vXixdEGiNO6O1FXrexoMtZ5e');

-- Assign admin role
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u,
     roles r
WHERE u.username = 'onevoker'
  AND r.name IN ('ROLE_User', 'ROLE_Admin');