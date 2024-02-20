package com.example.app.mtcg.entity;

import java.util.Vector;

public class Deck {
    private Vector<Card> deck;

    public Vector<Card> getDeck() {
        return deck;
    }

    public void setDeck(Vector<Card> deck) {
        this.deck = deck;
    }

    public Deck(Vector<Card> deck) {
        this.deck = deck;
    }
}
