CREATE TABLE product(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    brand_id INTEGER NOT NULL,
    available_amount INTEGER NOT NULL,
    discount INTEGER,
    date_added TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);