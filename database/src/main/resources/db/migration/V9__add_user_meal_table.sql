
CREATE Table USER_MEAL_COLLECTION(
    id int NOT NULL AUTO_INCREMENT,
    collection_name VARCHAR(255) NOT NULL,
    fk_user_id int NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (fk_user_id) REFERENCES USERS(id) ON DELETE CASCADE
);

CREATE Table USER_MEALS (
  id int NOT NULL AUTO_INCREMENT,
  meal_name VARCHAR(255) NOT NULL,
  is_disabled int NOT NULL,
  fk_collection_id int NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (fk_collection_id) REFERENCES USER_MEAL_COLLECTION(id) ON DELETE CASCADE,
  CONSTRAINT UC_category_name UNIQUE (meal_name)
);