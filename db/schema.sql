DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS candidate;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS post
(
    id      SERIAL PRIMARY KEY,
    name    TEXT,
    created TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS city
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE IF NOT EXISTS candidate
(
    id      SERIAL PRIMARY KEY,
    city_id INT references city (id),
    name    TEXT,
    created TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    name     TEXT,
    email    TEXT UNIQUE,
    password TEXT
);

INSERT INTO city(name)
VALUES ('Saint-Petersburg');
INSERT INTO city(name)
VALUES ('Bryansk');
INSERT INTO city(name)
VALUES ('Vologda');
