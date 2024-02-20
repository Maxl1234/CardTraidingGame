package com.example.app.mtcg.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Vector;

public class User {

    private int id;
    private int currency = 20;
    private String username;
    private String password;
    private String authToken;
    private Vector<Card> userCards;
    private Deck userDeck;

    public User() {

    }

    public User(int id, int currency, String username, String password, String authToken, Vector<Card> userCards, Deck userDeck) {
        this.id = id;
        this.currency = currency;
        this.username = username;
        this.password = password;
        this.authToken = authToken;
        this.userCards = userCards;
        this.userDeck = userDeck;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, int currency, String username, String password, String authToken) {
        this.id = id;
        this.currency = currency;
        this.username = username;
        this.password = password;
        this.authToken = authToken;
    }
    public void addPackage(CardPackage pack){
        for(Card card : pack.getCardPack()){
            this.userCards.add(card);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Vector<Card> getUserCards() {
        return userCards;
    }

    public void setUserCards(Vector<Card> userCards) {
        this.userCards = userCards;
    }

    public Deck getUserDeck() {
        return userDeck;
    }

    public void setUserDeck(Deck userDeck) {
        this.userDeck = userDeck;
    }
}