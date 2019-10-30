ALTER table MEAL_OPTIONS
    ADD COLUMN description VARCHAR(500);

ALTER table MEAL_OPTIONS
    ADD COLUMN main_ingredients VARCHAR(500);

CREATE Table MEAL_OPTION_RECIPES
(
    id int NOT NULL AUTO_INCREMENT,
    fk_meal_option INT,
    title varchar(150),
    ingredients varchar(500),
    instructions varchar(2000),

    PRIMARY KEY (id),
    FOREIGN KEY (fk_meal_option) REFERENCES MEAL_OPTIONS(id)
);