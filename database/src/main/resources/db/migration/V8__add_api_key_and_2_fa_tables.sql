CREATE TABLE API_KEYS (
  id INT NOT NULL AUTO_INCREMENT,
  api_key_string VARCHAR(36) NOT NULL,
  fk_user_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (fk_user_id) REFERENCES USERS(id) ON DELETE CASCADE,
  CONSTRAINT UC_api_key_string UNIQUE (api_key_string)
);

CREATE TABLE API_KEY_USAGE (
  id INT NOT NULL AUTO_INCREMENT,
  usage_count INT NOT NULL DEFAULT 0,
  last_usage TIMESTAMP,
  fk_user_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (fk_user_id) REFERENCES USERS(id) ON DELETE CASCADE,
  CONSTRAINT UC_usage_user_id UNIQUE(fk_user_id)
);

CREATE Table TWO_FACTOR_AUTHENTICATION_TOKENS (
  id INT NOT NULL AUTO_INCREMENT,
  external_user_id VARCHAR(20) NOT NULL,
  token VARCHAR(10) NOT NULL,
  expiry_time TIMESTAMP NOT NULL,
  fk_user_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (fk_user_id) REFERENCES USERS(id) ON DELETE CASCADE,
  CONSTRAINT UC_two_factor_authentication UNIQUE (fk_user_id, external_user_id)
);

CREATE EVENT IF NOT EXISTS delete_expired_2fa_keys
ON SCHEDULE EVERY 30 MINUTE
DO
DELETE FROM TWO_FACTOR_AUTHENTICATION_TOKENS where expiry_time < now();