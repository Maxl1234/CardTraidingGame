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

    public void connectdb() {
        try {


            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung zur DB hergestellt.");
        } catch (SQLException e) {

            System.err.println("Fehler beim Verbinden zur Datenbank: " + e.getMessage());
        }
    }

    public void disconectdb() {
        try {
            connection.close();
            System.out.println("Verbindung zu DB geschlossen ");
        } catch (SQLException e) {

            System.err.println("Problem beim DB schließen" + e.getMessage());
        }
    }

    public User getUser(String searchedUser) {
        String username = "", password = "";
        int id = -1, currency = -1;
        try {
            PreparedStatement stmnt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmnt.setString(1, searchedUser);
            ResultSet results = stmnt.executeQuery();

            if (results == null) return null;

            while (results.next()) {
                username = results.getString("username");
                password = results.getString("password");
                id = results.getInt("user_id");
                currency = results.getInt("currency");
            }
            results.close();
            stmnt.close();

            if (!username.isEmpty() && !password.isEmpty() && currency >= 0) {
                return new User(id, currency, username, password, username + "-mtcgToken");
            }
        } catch (SQLException e) {
            System.err.println("Fetching User failed" + e.getSQLState());
        }
        return null;
    }

    public CardPackage getPackage() {
        int package_id = 0;
        CardPackage pack = new CardPackage();
        String getQuerry = "SELECT * FROM packages LIMIT 1";

        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            ResultSet result = stmnt.executeQuery();

            while (result.next()) {
                package_id = result.getInt("package_id");
            }
            String name, type, id, element;
            double damage;
            ArrayList<Card> cardsList = new ArrayList<Card>();
            getQuerry = "SELECT * FROM cards " +
                    "INNER JOIN packages_cards ON cards.card_id = packages_cards.card_id " +
                    "WHERE package_id = ?";

            stmnt = connection.prepareStatement(getQuerry);
            stmnt.setInt(1, package_id);
            result = stmnt.executeQuery();

            while (result.next()) {
                id = result.getString("card_id");
                name = result.getString("name");
                element = result.getString("element");
                type = result.getString("type");
                damage = result.getDouble("damage");
                cardsList.add(new Card(name, element, type, damage, id));

            }
            pack.addCards(cardsList);
            pack.setId(package_id);

            result.close();
            stmnt.close();
            return pack;
        } catch (SQLException e) {
            System.err.println("getPackage failed " + e.getMessage());
        }
        return null;

    }
    public boolean deletePackage(CardPackage packToDel){
        boolean succ = false;
            String deleteQuerry = "DELETE FROM packages_cards WHERE package_id = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(deleteQuerry);
            stmnt.setInt(1,packToDel.getId());
            int row = stmnt.executeUpdate();
            if(row < 1){
                System.err.println("delete packages_cards failed");
                return false;
            }
            deleteQuerry = "DELETE FROM packages WHERE package_id = ?";
            stmnt = connection.prepareStatement(deleteQuerry);
            stmnt.setInt(1,packToDel.getId());
            row = stmnt.executeUpdate();
            if(row < 1){
                System.err.println("delete packagesfailed");
                return false;
            }
            succ = true;
            stmnt.close();

        }
        catch (SQLException e){
            System.err.println("delete package fail"+e.getMessage());
        }
        return succ;
    }

    public boolean updateUserBuyPack(User user) {
        boolean success = false;
        String updateQuery = "UPDATE users SET currency = ? WHERE user_id = ?";

        try {
            // Update currency
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, user.getCurrency());
            updateStatement.setInt(2, user.getId());
            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                // Karten in DB hinzufügen
                Vector<Card> cardToAddCards = new Vector<>();
                Vector<Card> dbCards = getCards(user);

                for (Card card : user.getUserCards()) {
                    boolean found = false;
                    for (Card card1 : dbCards) {
                        if (card.getId().equals(card1.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cardToAddCards.add(card);
                    }
                }

                if (!cardToAddCards.isEmpty()) {
                    String insertQuery = "INSERT INTO users_cards (card_id, user_id) VALUES (?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

                    for (Card addCard : cardToAddCards) {
                        insertStatement.setString(1, addCard.getId());
                        insertStatement.setInt(2, user.getId());
                        insertStatement.executeUpdate();
                    }

                    // Abrufen der generierten deck_id
                    String selectQuery = "SELECT deck_id FROM deck WHERE user_id = ?";
                    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                    selectStatement.setInt(1, user.getId());
                    ResultSet generatedKeys = selectStatement.executeQuery();

                    if (generatedKeys.next()) {
                        int deckId = generatedKeys.getInt(1);

                        // Lösche alte Karten aus deck_cards
                        String deleteDeckCardsQuery = "DELETE FROM deck_cards WHERE deck_id = ?";
                        PreparedStatement deleteDeckCardsStatement = connection.prepareStatement(deleteDeckCardsQuery);
                        deleteDeckCardsStatement.setInt(1, deckId);
                        deleteDeckCardsStatement.executeUpdate();

                        // Füge neue Karten in deck_cards ein
                        for (Card card : user.getUserDeck().getDeck()) {
                            String cardId = card.getId();
                            String insertDeckCardsQuery = "INSERT INTO deck_cards (deck_id, card_id) VALUES (?, ?)";
                            PreparedStatement insertDeckCardsStatement = connection.prepareStatement(insertDeckCardsQuery);
                            insertDeckCardsStatement.setInt(1, deckId);
                            insertDeckCardsStatement.setString(2, cardId);
                            insertDeckCardsStatement.executeUpdate();
                        }

                        success = true;
                    } else {
                        System.err.println("Error: Keine generierte deck_id gefunden.");
                    }

                    generatedKeys.close();
                    selectStatement.close();
                    insertStatement.close();
                }
            }

            updateStatement.close();
        } catch (SQLException e) {
            System.err.println("Update user failed: " + e.getMessage());
        }

        return success;
    }



    public User getUserByAuth(String AuthCor) {
        String username = "", password = "";
        int currency = 0, id = 0;

        String getQuerry = "SELECT * FROM users " +
                "JOIN userAuth ON users.user_id = userAuth.user_id " +
                "WHERE userAuth.token = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            stmnt.setString(1, AuthCor);
            ResultSet result = stmnt.executeQuery();

            while (result.next()) {
                username = result.getString("username");
                password = result.getString("password");
                currency = result.getInt("currency");
                id = result.getInt("user_id");
            }
            User user = new User(id, currency, username, password, AuthCor);
            stmnt.close();
            result.close();

            user.setUserCards(getCards(user));
            user.setUserDeck(getDeck(user));
            // selbe noch mit Deck

            if (!username.isEmpty() && !password.isEmpty() && currency >= 0) {
                return user;
            }

        } catch (SQLException e) {
            System.err.println("getUser failed" + e.getMessage());
        }
        return null;
    }

    public Deck getDeck(User user){
        Deck deck = new Deck();
        String selectQuerry = "SELECT deck_id FROM deck WHERE user_id = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(selectQuerry);
            stmnt.setInt(1,user.getId());
            ResultSet result = stmnt.executeQuery();
            if(result.next()) {
                int deckId = result.getInt("deck_id");
                selectQuerry = "SELECT * FROM deck_cards WHERE deck_id = ?";
                stmnt = connection.prepareStatement(selectQuerry);
                stmnt.setInt(1, deckId);
                result = stmnt.executeQuery();
                Vector<String> cardIdVec = new Vector<>();

                while (result.next()) {
                    cardIdVec.add(result.getString("card_id"));
                }
                if (cardIdVec.isEmpty()) {
                    System.out.println("User has no deck yet");
                    return deck;
                }
                selectQuerry = "SELECT * FROM cards WHERE card_id = ?";
                Vector<Card> cardsToAdd = new Vector<>();
                for (String cardId : cardIdVec) {
                    stmnt = connection.prepareStatement(selectQuerry);
                    stmnt.setString(1, cardId);
                    result = stmnt.executeQuery();

                    if (result.next()) {
                        String name = result.getString("name");
                        String element = result.getString("element");
                        String type = result.getString("type");
                        double damage = result.getDouble("damage");
                        cardsToAdd.add(new Card(name, element, type, damage, cardId));
                    }

                }
                if (cardsToAdd.isEmpty()) {
                    System.err.println("Card add to Deck failed");
                    return deck;
                }
                deck.setDeck(cardsToAdd);
                stmnt.close();
                result.close();
            }
        }
        catch (SQLException e){
            System.err.println("fetch deck failed "+e.getMessage());
        }
    return deck;
    }


    public Vector<Card> getCards(User user) {
        Vector<Card> userCards = new Vector<Card>();
        String name, element, type, id;
        double damage;
        String getQuerry = "SELECT * FROM cards " +
                "JOIN users_cards ON cards.card_id = users_cards.card_id " +
                "WHERE users_cards.user_id = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            stmnt.setInt(1, user.getId());

            ResultSet result = stmnt.executeQuery();

            while (result.next()) {
                id = result.getString("card_id");
                name = result.getString("name");
                element = result.getString("element");
                type = result.getString("type");
                damage = result.getDouble("damage");

                userCards.add(new Card(name, element, type, damage, id));

            }
            result.close();
            stmnt.close();

        } catch (SQLException e) {
            System.err.println("getCards failed" + e.getMessage());
        }
        return userCards;
    }

    public boolean insertToken(User user) {
        boolean auth = false;

        try {
            int id = user.getId();
            String token = user.getAuthToken();
            PreparedStatement stmnt = connection.prepareStatement("INSERT INTO userAuth (token,user_id) VALUES (?,?)");
            stmnt.setString(1, token);
            stmnt.setInt(2, id);

            int row = stmnt.executeUpdate();
            if (row > 0) {
                auth = true;
            }
            connection.close();
            stmnt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return auth;

    }

    public boolean checkAuth(String AuthCor) {
        boolean suc = false;
        String getQuerry = "SELECT * FROM userAuth WHERE token = ?";
        try {
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            stmnt.setString(1, AuthCor.trim());
            ResultSet result = stmnt.executeQuery();
            suc = result.isBeforeFirst();
            stmnt.close();

        } catch (SQLException e) {
            System.err.println("checkAuth failes " + e.getMessage());
        }

        return suc;
    }

    public boolean insertCard(Card card) {
        String id = card.getId();
        String name = card.getName();
        String element = card.getElement();
        String type = card.getType();
        double damage = card.getDamage();

        String insertQuery = "INSERT INTO cards (card_id,name,element,type,damage) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement stmnt = connection.prepareStatement(insertQuery);
            stmnt.setObject(1, id);
            stmnt.setString(2, name);
            stmnt.setString(3, element);
            stmnt.setString(4, type);
            stmnt.setDouble(5, damage);

            int rowsAf = stmnt.executeUpdate();
            if (rowsAf > 0) {
                System.out.println("Card insert success");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Card insert Error" + e.getSQLState());
        }
        return false;

    }

    public boolean insertPackage(List<Card> cardsToInsert) {

        for (Card card : cardsToInsert) {
            boolean cardSucc = insertCard(card);
            if (!cardSucc) return false;
        }
        String insertQuerry = "INSERT INTO packages DEFAULT VALUES RETURNING package_id";

        int id = 0;

        try {
            PreparedStatement stmnt = connection.prepareStatement(insertQuerry);
            ResultSet result = stmnt.executeQuery();

            if (result.next()) {
                id = result.getInt("package_id");
            } else {
                System.err.println("Failed to retrieve generated package ID");
                return false;
            }


        } catch (SQLException e) {
            System.err.println("Package insert failed" + e.getSQLState());
            return false;
        }
        String relationQuery = "INSERT INTO packages_cards (package_id, card_id) VALUES (?, ?)";
        // Irgendwas will es hier gar nicht

        try (PreparedStatement stmnt = connection.prepareStatement(relationQuery)) {
            for (Card card : cardsToInsert) {
                stmnt.setInt(1, id);
                stmnt.setObject(2, card.getId());
                stmnt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Pack Card Rel FAIL" + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean insertUser(User user) {
        String un = user.getUsername();
        String pw = user.getPassword();
        String selectQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertUserQuery = "INSERT INTO users (username, password, currency) VALUES (?, ?, ?)";
        String insertDeckQuery = "INSERT INTO deck (user_id) VALUES (?)";

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

            // Führe die INSERT-Abfrage für den Benutzer aus
            PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
            insertUserStatement.setString(1, un);
            insertUserStatement.setString(2, pw);
            insertUserStatement.setInt(3, 20);

            int affectedRowsUser = insertUserStatement.executeUpdate();

            if (affectedRowsUser > 0) {
                System.out.println("Benutzer erfolgreich in die Datenbank eingefügt.");

                // Abrufen der generierten user_id
                ResultSet generatedKeys = insertUserStatement.getGeneratedKeys();
                int userId;

                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);

                    // Führe die INSERT-Abfrage für das Deck aus
                    PreparedStatement insertDeckStatement = connection.prepareStatement(insertDeckQuery);
                    insertDeckStatement.setInt(1, userId);

                    int affectedRowsDeck = insertDeckStatement.executeUpdate();

                    if (affectedRowsDeck > 0) {
                        System.out.println("Deck erfolgreich in die Datenbank eingefügt.");
                    } else {
                        System.err.println("Deck konnte nicht in die Datenbank eingefügt werden.");
                        return false;
                    }
                } else {
                    System.err.println("Benutzer konnte nicht in die Datenbank eingefügt werden (keine generierte user_id).");
                    return false;
                }
            } else {
                System.out.println("Benutzer konnte nicht in die Datenbank eingefügt werden.");
                return false;
            }

            // Wichtig: Schließe die Prepared Statements, um Ressourcen freizugeben
            selectStatement.close();
            insertUserStatement.close();
        } catch (SQLException e) {
            System.err.println("User insert gescheitert. Fehlerdetails:");
            System.err.println("SQL-Statuscode: " + e.getMessage());
            return false;
        }
        return true;
    }
}


