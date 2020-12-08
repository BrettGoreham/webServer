CREATE TABLE USERS (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(60) NOT NULL,
    email VARCHAR(50) NOT NULL,
    role_list VARCHAR(20),
    enabled boolean DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT UC_username UNIQUE (username),
    CONSTRAINT UC_email UNIQUE (email)

);

CREATE TABLE USER_CONFIRMATION_TOKENS(
    id INT NOT NULL AUTO_INCREMENT,
    fk_user INT NOT NULL,
    token VARCHAR(36) NOT NULL,
    registration_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UC_user_id_fk UNIQUE (fk_user),
    PRIMARY KEY (id),
    FOREIGN KEY (fk_user) REFERENCES USERS(id)
)