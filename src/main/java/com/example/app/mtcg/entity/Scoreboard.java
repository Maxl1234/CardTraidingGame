package com.example.app.mtcg.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Scoreboard {
    private Vector<User> userScores = new Vector<>();

    public Scoreboard(Vector<User> userScores) {
        this.userScores = userScores;
    }
    public void sortUserScoresByScore() {
        // Comparator erstellen, um Benutzer nach Punkten zu sortieren
        Comparator<User> scoreComparator = Comparator.comparingInt(User::getScore).reversed();

        // Sortieren Sie den Vektor userScores mit dem erstellten Comparator
        Collections.sort(userScores, scoreComparator);
    }

    public void addUser(User userToAdd){
        Vector<User> newScoreboard = new Vector<>();
        if (userScores.isEmpty()){
            userScores.add(userToAdd);
        }
        else {
            int y = 0;
            boolean added = false;
            for (int i = 0; i <= userScores.size(); i++) {
                if (userScores.elementAt(i - y).getScore() > userToAdd.getScore() || added) {
                    newScoreboard.add(userScores.elementAt(i - y));
                } else {
                    newScoreboard.add(userToAdd);
                    added = true;
                    y = 1;
                }
            }

            userScores=newScoreboard;
        }
    }

    public String showScores (){
        String output ="";
        for(User user: userScores){
            output+="Username: "+user.getUsername()+" Score: "+user.getScore()+"\n";
        }
        return output;
    }

    public Vector<User> getUserScores() {
        return userScores;
    }

    public void setUserScores(Vector<User> userScores) {
        this.userScores = userScores;
    }
}
