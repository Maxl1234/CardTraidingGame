package com.example.app.mtcg.entity;

import java.util.Random;
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

    public Card getRndCard(){
        Random rand = new Random();
        int randInt = rand.nextInt(this.deck.size());
        return deck.elementAt(randInt);
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
    public void addCard(Card card){
        this.deck.add(card);
    }
    public void deleteCard(Card cardToRm){

        for(int i = 0;i<this.deck.size();i++){
            if(this.deck.elementAt(i).getId().equals(cardToRm.getId())){
                this.deck.remove(i);
            }
        }
    }

}
