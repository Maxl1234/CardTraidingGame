package com.example.app.mtcg.db;

import com.example.app.mtcg.entity.*;

import java.sql.*;


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

