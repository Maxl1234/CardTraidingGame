CREATE TABLE users (
  user_id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(50) NOT NULL,
    currency INT
);
CREATE TABLE userAuth(
    token_id SERIAL PRIMARY KEY,
    token VARCHAR(50),
    user_id INT REFERENCES users(user_id)
);


CREATE TABLE cards (
    card_id UUID PRIMARY KEY,
    name VARCHAR(50),
    element VARCHAR(50),
    type VARCHAR(50),
    damage FLOAT 
);
CREATE TABLE users_cards(
    user_id SERIAL REFERENCES users(user_id),
    card_id UUID REFERENCES cards(card_id),
    PRIMARY KEY(user_id,card_id)
);
CREATE TABLE deck( 
    deck_id SERIAL PRIMARY KEY,
    user_id SERIAL REFERENCES users(user_id)
    );
CREATE TABLE deck_cards(
    deck_id SERIAL REFERENCES deck(deck_id),
    card_id UUID REFERENCES cards(card_id),
    PRIMARY KEY(deck_id,card_id)
);
CREATE TABLE packages(
    package_id SERIAL PRIMARY KEY
);
CREATE TABLE packages_cards(
    package_id SERIAL REFERENCES packages(package_id),
    card_id UUID REFERENCES cards(card_id),
    PRIMARY KEY(package_id,card_id)
);