CREATE TABLE users
(
    id       INTEGER      GENERATED BY DEFAULT AS IDENTITY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN      NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE authorities
(
    id        INTEGER      GENERATED BY DEFAULT AS IDENTITY,
    username  VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);