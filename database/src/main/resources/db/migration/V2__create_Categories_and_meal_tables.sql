CREATE TABLE MEAL_CATEGORIES (
  id INT NOT NULL AUTO_INCREMENT,
  category_name VARCHAR(255) NOT NULL,
  status VARCHAR(20),
  PRIMARY KEY (id),
  CONSTRAINT UC_category_name UNIQUE (category_name)
);

CREATE Table MEAL_OPTIONS (
  id int NOT NULL AUTO_INCREMENT,
  meal_name VARCHAR(255) NOT NULL,
  status VARCHAR(20),
  fk_meal_category INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (fk_meal_category) REFERENCES MEAL_CATEGORIES(id),
  CONSTRAINT UC_category_name UNIQUE (meal_name, fk_meal_category)
);