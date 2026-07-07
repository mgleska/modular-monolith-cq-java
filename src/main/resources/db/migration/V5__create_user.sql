CREATE TABLE usr_user
(
    id         INT AUTO_INCREMENT          NOT NULL,
    email      VARCHAR(180)                NOT NULL,
    name       VARCHAR(250)                NULL,
    password   VARCHAR(255)                NULL,
    token      VARCHAR(200)                NULL,
    status     ENUM ('ACTIVE', 'INACTIVE') NOT NULL,
    created_at DATETIME                    NULL,
    updated_at DATETIME                    NULL,
    PRIMARY KEY (id)
);

CREATE TABLE usr_user_role
(
    id      INT AUTO_INCREMENT               NOT NULL,
    user_id INT                              NOT NULL,
    `role`  ENUM ('ROLE_USER', 'ROLE_ADMIN') NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE usr_user
    ADD CONSTRAINT uc_usr_user_email UNIQUE (email);

ALTER TABLE usr_user
    ADD CONSTRAINT uc_usr_user_token UNIQUE (token);

ALTER TABLE usr_user_role
    ADD CONSTRAINT FK_USR_USER_ROLE_ON_USER FOREIGN KEY (user_id) REFERENCES usr_user (id);