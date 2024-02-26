package com.example.app.mtcg.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Vector;

public class User {

    private int id;
    private int currency = 20;
    private int score = 100;
    int wins = 0;
    int losses = 0;
    int draws = 0;
    private String username;
    private String password;
    private String authToken;
    private String name = "";
    private String bio = "";
    private String image = "";
    private Deck userDeck;
    private Vector<Card> userCards = new Vector<>();

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

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.authToken = username + "-mtcgToken";
        this.userDeck = new Deck();


    }

    public User(int id, int currency, String username, String password, String authToken) {
        this.id = id;
        this.currency = currency;
        this.username = username;
        this.password = password;
        this.authToken = authToken;
        this.userDeck = new Deck();
    }
    public User(int id, int currency, String username, String password, String authToken,String bio,String image,String name,int score,int wins,int losses,int draws) {
        this.id = id;
        this.currency = currency;
        this.username = username;
        this.password = password;
        this.authToken = authToken;
        this.userDeck = new Deck();
        this.image=image;
        this.bio=bio;
        this.name=name;
        this.score = score;
        this.draws=draws;
        this.wins=wins;
        this.losses=losses;
    }



    public void updateDeck(){
        if(this.userDeck==null || this.userDeck.getDeck().isEmpty()){
            userDeck.setRndDeck(userCards);
        }
    }

    public void addPackage(CardPackage pack){
        for(Card card : pack.getCardPack()){
            this.userCards.add(card);
        }
    }
    public void addCards(Vector<Card> cardstoAdd){
        for(Card card:cardstoAdd){
            this.userCards.add(card);
        }
    }
    public void deleteCards(Vector<Card> cardstoDel){
        for (int i = 0;i<this.userCards.size();i++){
            for (int y = 0;y<cardstoDel.size();i++)
            if(this.userCards.elementAt(i).getId().equals(cardstoDel.elementAt(y).getId())){
                this.userCards.remove(i);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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