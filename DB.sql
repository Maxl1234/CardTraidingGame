CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    currency INT,
    bio VARCHAR("255"),
    image VARCHAR("255"),
    name VARCHAR("255"),
    score INT,
    wins INT,
    losses INT,
    draws INT
);
CREATE TABLE userAuth(
    token_id SERIAL PRIMARY KEY,
    token VARCHAR(50),
    user_id INT REFERENCES users(user_id)
);
CREATE TABLE scoreboard(
    user_id SERIAL REFERENCES users(user_id),
    score INT,
    PRIMARY KEY (user_id,score)
);


CREATE TABLE cards (
    card_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50),
    element VARCHAR(50),
    type VARCHAR(50),
    damage DOUBLE PRECISION 
);
CREATE TABLE users_cards(
    user_id SERIAL REFERENCES users(user_id),
    card_id VARCHAR(50) REFERENCES cards(card_id),
    PRIMARY KEY(user_id,card_id)
);
CREATE TABLE deck( 
    deck_id SERIAL PRIMARY KEY,
    user_id SERIAL REFERENCES users(user_id)
    );
CREATE TABLE deck_cards(
    deck_id SERIAL REFERENCES deck(deck_id),
    card_id VARCHAR(50) REFERENCES cards(card_id),
    PRIMARY KEY(deck_id,card_id)
);
CREATE TABLE packages(
    package_id SERIAL PRIMARY KEY
);
CREATE TABLE packages_cards(
    package_id SERIAL REFERENCES packages(package_id),
    card_id VARCHAR(50) REFERENCES cards(card_id),
    PRIMARY KEY(package_id,card_id)
);
CREATE TABLE scoreboard(
    user_id SERIAL,
    score INT,
    PRIMARY KEY(user_id,score)
)
DELETE FROM userAuth;
DELETE FROM users_cards;
DELETE FROM deck_cards;
DELETE FROM deck;
DELETE FROM packages_cards;
DELETE FROM packages;
DELETE FROM cards;
DELETE FROM scoreboard;
Delete from users;