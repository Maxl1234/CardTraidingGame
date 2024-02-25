package com.example.app.mtcg.entity;

import java.util.Random;

public class Battle {
    private final User userA,userB;
    private int maxGames = 100;
    private int playedGames = 0;
    private String log = "";

    public Battle(User userA, User userB) {
        this.userA = userA;
        this.userB = userB;
    }

    public String startBattle(){
        Deck deckA = this.userA.getUserDeck();
        Deck deckB = this.userB.getUserDeck();

        User winner = null;

        if(deckA.getDeck().size()!=4 || deckB.getDeck().size()!=4){
            this.log +="One of two decks are to small in Battle";
            return this.log;
        }
        Card battleCardA = null;
        Card battleCardB = null;
        Card winnerCard = null;

        this.log += "\nBattle starts now";
        this.log += "\nPlayerA: "+this.userA.getUsername()+" vs PlayerB: "+this.userB.getUsername();

        while (maxGames>0 && winner==null){

            if (deckA.getDeck().size() == 0) {

                winner = this.userB;
                // Update elo in DB
                this.log += "\nPlayer B won.";
                break;
            } else if (deckB.getDeck().size() == 0) {

                winner = this.userA;
                //update elo in DB
                this.log += "\nPlayer B won.";
                break;
            }

            battleCardA = deckA.getRndCard();
            battleCardB = deckB.getRndCard();
            winnerCard = null;
            this.log += "\nRound: "+(100-maxGames)+" PlayerA plays: "+battleCardA.getName()+" vs the card of PlayerB: "+battleCardB.getName();

            switch (getSpecial(battleCardA,battleCardB)){
                case 1:
                    winnerCard = battleCardA;
                    this.log += "\n Insta kill by A";
                    break;

                case 2:
                    winnerCard = battleCardB;
                    this.log += "\n Insta kill by B";
                    break;

                case 0:
                    if(battleCardA.getType().equals("Spell") || battleCardB.getType().equals("Spell")){
                        double damageA = getDamage(battleCardA,battleCardB);
                        double damageB = getDamage(battleCardB,battleCardA);
                        this.log += "\nSpezial Damage";
                        this.log += "\nCard A with "+damageA+" damage and Card B with "+damageB+" damage";
                        if(damageA>damageB){
                            winnerCard=battleCardA;
                        }
                        else if(damageA<damageB){
                            winnerCard=battleCardB;
                        }
                    }
                    else {
                        this.log += "\nCard A with "+battleCardA.getDamage()+" damage and Card B with "+battleCardB.getDamage()+" damage";
                        if (battleCardA.getDamage() > battleCardB.getDamage()) {
                            winnerCard = battleCardA;
                        } else if (battleCardA.getDamage() < battleCardB.getDamage()) {
                            winnerCard = battleCardB;
                        }
                    }
                    break;
            }
            if(winnerCard == battleCardA){
                this.log+= "\nPlayer A won this round";
                deckA.addCard(battleCardB);
                deckB.deleteCard(battleCardB);
            }
            else if(winnerCard == battleCardB){
                this.log+= "\nPlayer B won this round";
                deckB.addCard(battleCardA);
                deckA.deleteCard(battleCardA);
            }
            else if(winnerCard == null){
                this.log += "\nThis round was a draw";
            }
            this.log+="\n";
            maxGames--;
        }
        if (winner==this.userA) {
            this.log += "\nThe winner of the Battle is Player A";
            this.userA.addCards(userB.getUserDeck().getDeck());
            this.userB.deleteCards(userB.getUserDeck().getDeck());
            this.userB.setUserDeck(new Deck());
            userA.setWins(userA.getWins()+1);
            userB.setLosses(userB.getLosses()+1);
        }
        else if(winner==this.userB){
            this.log+= "\nThe winner of the Battle is PlayerB";
            this.userB.addCards((this.userA.getUserDeck().getDeck()));
            this.userA.deleteCards(userA.getUserDeck().getDeck());
            this.userA.setUserDeck(new Deck());
            userB.setWins(userB.getWins()+1);
            userA.setLosses(userA.getLosses()+1);
            }
        else{
            this.log+="\n Todays fight was a draw";
            userA.setDraws(userA.getDraws()+1);
            userB.setDraws(userB.getDraws()+1);
        }

    return this.log;
    }

    public double getDamage(Card me, Card op){
        if((me.getElement()=="Water" && op.getElement()=="Fire")||
                (me.getElement()=="Fire" && op.getElement()=="Normal")||
                (me.getElement()=="Normal" && op.getElement() == "Water")){

            return me.getDamage()*2;
        }
        else if((op.getElement()=="Water" && me.getElement()=="Fire")||
                (op.getElement()=="Fire" && me.getElement()=="Normal")||
                (op.getElement()=="Normal" && me.getElement() == "Water")){
            return me.getDamage()*0.5;
        }
        else
            return me.getDamage();
    }

    public int getSpecial(Card me, Card op){
        if( (me.getName().contains("Goblin") && op.getName().contains("Dragon")) ||
                (me.getName().contains("Ork") && op.getName().contains("Wizard")) ||
                (me.getName().contains("Knight") && (op.getElement().equals("Water") && op.getType().equals("Spell"))) ||
                (me.getType().equals("Spell") && op.getName().contains("Kraken")) ||
                (me.getName().contains("Dragon") && op.getName().contains("FireElf"))

        ) {
            return 2;
        }

        else if( (op.getName().contains("Goblin") && me.getName().contains("Dragon")) ||
                (op.getName().contains("Ork") && me.getName().contains("Wizard")) ||
                (op.getName().contains("Knight") && (me.getElement().equals("Water") && me.getType().equals("Spell"))) ||
                (op.getType().equals("Spell") && me.getName().contains("Kraken")) ||
                (op.getName().contains("Dragon") && me.getName().contains("FireElf"))

        ) {
            return 1;
        }


        return 0;
    }



    public User getUserA() {
        return userA;
    }

    public User getUserB() {
        return userB;
    }

    public int getMaxGames() {
        return maxGames;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(int playedGames) {
        this.playedGames = playedGames;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
