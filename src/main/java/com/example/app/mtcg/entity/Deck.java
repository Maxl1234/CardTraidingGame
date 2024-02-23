package com.example.app.mtcg.entity;

import java.util.Vector;

public class Deck {
    private int id;
    private Vector<Card> deck ;

    public void setRndDeck(Vector<Card> cards){
        Vector<Card> cardDeck = new Vector<>();
        for(int i = 0; i<2;i++){
            cardDeck.add(cards.get(i));
            cardDeck.add(cards.get((cards.size()-1)-i));
        }
        this.deck=cardDeck;
    }

    public Deck() {
        deck=new Vector<>();
    }

    public Vector<Card> getDeck() {
        return deck;
    }

    public void setDeck(Vector<Card> deck) {
        this.deck = deck;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
