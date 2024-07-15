CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS roles
(
    id   SERIAL,
    name VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES roles (id) ON DELETE CASCADE,

    CONSTRAINT unique_user_role_pair UNIQUE (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS projects
(
    id   SERIAL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS records
(
    id          SERIAL,
    user_id     INTEGER                  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    project_id  INTEGER                  NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    hours       INTEGER                  NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users_projects
(
    user_id    INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    project_id INTEGER NOT NULL REFERENCES projects (id) ON DELETE CASCADE,

    CONSTRAINT unique_user_project_pair UNIQUE (user_id, project_id)
);