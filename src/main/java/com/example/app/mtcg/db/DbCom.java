package com.example.app.mtcg.db;

import com.example.app.mtcg.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import java.util.Vector;


public class DbCom {

    private final String url = "jdbc:postgresql://localhost:5432/mtcgdb";
    private final String user = "postgres";
    private final String password = "postgres";

    private Connection connection = null;

    public void connectdb(){
        try {


            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung zur DB hergestellt.");
        } catch (SQLException  e) {

            System.err.println("Fehler beim Verbinden zur Datenbank: " + e.getMessage());
        }
    }

    public void disconectdb(){
        try {
            connection.close();
            System.out.println("Verbindung zu DB geschlossen ");
        }
        catch (SQLException e){

            System.err.println("Problem beim DB schließen"+ e.getMessage());
        }
    }

    public User getUser(String searchedUser)  {
        String username = "", password = "";
        int id = -1, currency = -1;
        try{
            PreparedStatement stmnt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmnt.setString(1,searchedUser);
            ResultSet results = stmnt.executeQuery();

            if(results==null) return null;

            while (results.next()){
                username = results.getString("username");
                password = results.getString("password");
                id = results.getInt("user_id");
                currency = results.getInt("currency");
            }
            results.close();
            stmnt.close();

            if(!username.isEmpty() && !password.isEmpty() && currency >= 0){
                return new User(id,currency,username,password,username+"-mtcgToken");
            }
        }
        catch (SQLException e){
            System.err.println("Fetching User failed"+e.getSQLState());
        }
        return null;
    }
    public CardPackage getPackage (){
        int package_id=0;
        CardPackage pack = new CardPackage();
        String getQuerry = "SELECT * FROM package LIMIT 1";

        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            ResultSet result = stmnt.executeQuery();

            while(result.next()){
                package_id = result.getInt("package_id");
            }
            String name ,type,id,element;
            double damage;
            ArrayList<Card> cardsList = new ArrayList<Card>();
            getQuerry = "SELECT * FROM packages_cards" +
                    "INNER JOIN cards ON packages_cards.card_id = cards.card_id WHERE package_id = ?";

            stmnt = connection.prepareStatement(getQuerry);
            result = stmnt.executeQuery();

            while (result.next()){
                id = result.getString("card_id");
                name = result.getString("name");
                element = result.getString("element");
                type = result.getString("type");
                damage = result.getDouble("damage");
                cardsList.add(new Card(name, element, type, damage, id));

            }
            pack.addCards(cardsList);

            result.close();
            stmnt.close();
            return pack;
        }
        catch (SQLException e){
            System.err.println("getPackage failed"+e.getMessage());
        }
        return null;

    }
    public boolean updateUserBuyPack(User user) {           //anders umschreiben weil deck soll doch besten 5 karten haben
        Boolean succ = false;
        String updateQuerry = "UPDATE users SET currency = ? WHERE user_id = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(updateQuerry);
            stmnt.setInt(1, user.getCurrency());
            stmnt.setInt(2, user.getId());
            int row = stmnt.executeUpdate();

            if (row > 0) {
                String deleteQuerry = "DELETE FROM deck WHERE user_id = ?";
                stmnt = connection.prepareStatement(deleteQuerry);
                stmnt.setInt(1, user.getId());
                stmnt.executeUpdate();




                String inserQuerry = "INSERT INTO deck (user_id) VALUES (?)";
                stmnt = connection.prepareStatement(inserQuerry);
                stmnt.setInt(1, user.getId());
                stmnt.executeUpdate();
                ResultSet result = stmnt.executeQuery();

                if (result.next()) {
                    int deckid = result.getInt(1);
                    for (Card card : user.getUserDeck().getDeck()) {
                        String id = card.getId();
                        stmnt = connection.prepareStatement("INSERT INTO deck_cards (deck_id, card_id) VALUES (?, ?)");
                        stmnt.setInt(1, deckid);
                        stmnt.setString(2, id);
                        stmnt.executeUpdate();
                    }
                }
                Vector<Card> newCards = new Vector<>();
                Vector<Card> oldCards = getCards(user);


                for (Card card : user.getUserCards()) {
                    for (Card card1  : oldCards) {
                        if (card.getId().equals(card1.getId())) break;
                    }
                    newCards.add(card);
                }

                if (newCards.isEmpty()) {
                    stmnt.close();
                    result.close();
                    return false;
                }
                inserQuerry = "INSERT INTO users_cards (car_did, user_id) VALUES (?, ?)";
                stmnt = connection.prepareStatement(inserQuerry);
                for (Card addCard : newCards) {
                    stmnt.setString(1, addCard.getId());
                    stmnt.setInt(2, user.getId());
                    stmnt.executeUpdate();
                }

                succ = true;
                result.close();
            }

            stmnt.close();


        } catch (SQLException e) {
            System.err.println("update user failed" + e.getMessage());
        }

        return succ;
    }

    public User getUserByAuth(String Auth){
        String username="",password="";
        int currency=0,id=0;
        String AuthCor = Auth.replace("Bearer","");
        String getQuerry = "SELECT * FROM users " +
                "JOIN userAuth ON user_id = user_id" +
                "WHERE token = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            stmnt.setString(1,AuthCor);
            ResultSet result = stmnt.executeQuery();

            while (result.next()){
                username = result.getString("username");
                password = result.getString("password");
                currency = result.getInt("currency");
                id = result.getInt("user_id");
            }
            User user = new User(id,currency,username,password, AuthCor);
            stmnt.close();
            result.close();

            user.setUserCards(getCards(user));
            // selbe noch mit Deck

            if(!username.isEmpty() && !password.isEmpty() && currency >= 0){
                return user;
            }

        }
        catch (SQLException e){
            System.err.println("getUser failed"+e.getMessage());
        }
        return null;
    }



    public Vector<Card> getCards (User user){
        Vector<Card> userCards = new Vector<Card>();
        String name,element,type,id;
        double damage;
        String getQuerry = "SELECT * FROM cards " +
                "JOIN users_cards ON card_id = card_id" +
                "WHERE user_id = ?";
        try{
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            stmnt.setInt(1,user.getId());

            ResultSet result = stmnt.executeQuery();

            while (result.next()){
                id = result.getString("card_id");
                name = result.getString("name");
                element = result.getString("element");
                type = result.getString("type");
                damage = result.getDouble("damage");

                userCards.add(new Card(name, element, type, damage, id));

            }
            result.close();
            stmnt.close();

        }
        catch (SQLException e){
            System.err.println("getCards failed"+e.getMessage());
        }
        return userCards;
    }

    public boolean insertToken (User user){
        boolean auth = false;

        try {
            int id = user.getId();
            String token = user.getAuthToken();
            PreparedStatement stmnt = connection.prepareStatement("INSERT INTO userAuth (token,user_id) VALUES (?,?)");
            stmnt.setString(1,token);
            stmnt.setInt(2,id);

            int row = stmnt.executeUpdate();
            if (row>0){
                auth = true;
            }
            connection.close();
            stmnt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return auth;

    }
    public boolean checkAuth(String Auth){
        boolean suc = false;
        String getQuerry = "SELECT * FROM userAuth WHERE token = ?";
        String AuthCor = Auth.replace("Bearer","");
        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            stmnt.setString(1,AuthCor);
            ResultSet result = stmnt.executeQuery();
            suc = result.isBeforeFirst();
            stmnt.close();

        }
        catch (SQLException e){
            System.err.println("checkAuth failes "+e.getMessage());
        }

        return suc;
    }

    public boolean insertCard(Card card){
        String id = card.getId();
        String name = card.getName();
        String element = card.getElement();
        String type = card.getType();
        double damage = card.getDamage();

        String insertQuery = "INSERT INTO cards (card_id,name,element,type,damage) VALUES (?,?,?,?,?)";
        try{
            PreparedStatement stmnt = connection.prepareStatement(insertQuery);
            stmnt.setObject(1,id);
            stmnt.setString(2,name);
            stmnt.setString(3,element);
            stmnt.setString(4,type);
            stmnt.setDouble(5,damage);

            int rowsAf = stmnt.executeUpdate();
            if(rowsAf>0){
                System.out.println("Card insert success");
                return true;
            }
        }
        catch (SQLException e){
            System.err.println("Card insert Error" + e.getSQLState());
        }
        return false;

    }

    public boolean insertPackage(List<Card> cardsToInsert){

        for (Card card : cardsToInsert){
            boolean cardSucc = insertCard(card);
            if (!cardSucc) return false;
        }
        String insertQuerry = "INSERT INTO packages DEFAULT VALUES RETURNING package_id";

        int id=0;

        try {
            PreparedStatement stmnt = connection.prepareStatement(insertQuerry);
            ResultSet result = stmnt.executeQuery();

            if (result.next()) {
                id = result.getInt("package_id");
            } else {
                System.err.println("Failed to retrieve generated package ID");
                return false;
            }


        }
        catch (SQLException e){
            System.err.println("Package insert failed"+e.getSQLState());
            return false;
        }
        String relationQuery = "INSERT INTO packages_cards (package_id, card_id) VALUES (?, ?)";
        // Irgendwas will es hier gar nicht

        try (PreparedStatement stmnt = connection.prepareStatement(relationQuery)) {
            for(Card card:cardsToInsert) {
                stmnt.setInt(1, id);
                stmnt.setObject(2, card.getId());
                stmnt.executeUpdate();
            }

        }
        catch (SQLException e){
            System.err.println("Pack Card Rel FAIL"+e.getMessage());
            return false;
        }

        return true;
    }

    public boolean insertUser(User user) {
        String un = user.getUsername();
        String pw = user.getPassword();
        String selectQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";

        try {
            // Überprüfe, ob der Benutzername bereits existiert
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, un);
            ResultSet resultSet = selectStatement.executeQuery();

            resultSet.next();
            int userCount = resultSet.getInt(1);

            if (userCount > 0) {
                System.err.println("Benutzername bereits vorhanden. Bitte wähle einen anderen.");
                return false; // Abbrechen, da der Benutzername bereits existiert
            }

            // Führe die INSERT-Abfrage aus, da der Benutzername nicht existiert
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, un);
            insertStatement.setString(2, pw);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Benutzer erfolgreich in die Datenbank eingefügt.");
            } else {
                System.out.println("Benutzer konnte nicht in die Datenbank eingefügt werden.");
                return false;
            }

            // Wichtig: Schließe die Prepared Statements, um Ressourcen freizugeben
            selectStatement.close();
            insertStatement.close();
        } catch (SQLException e) {
            System.err.println("User insert gescheitert. Fehlerdetails:");
            System.err.println("SQL-Statuscode: " + e.getSQLState());
            return false;
        }
        return true;
    }


}

