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
    };
    public void disconectdb(){
        try {
            connection.close();
            System.out.println("Verbindung zu DB geschlossen ");
        }
        catch (SQLException e){

            System.err.println("Problem beim DB schließen"+ e.getMessage());
        }
    }
    public void insertUser(User user) {
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
                return; // Abbrechen, da der Benutzername bereits existiert
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
            }

            // Wichtig: Schließe die Prepared Statements, um Ressourcen freizugeben
            selectStatement.close();
            insertStatement.close();
        } catch (SQLException e) {
            System.err.println("User insert gescheitert. Fehlerdetails:");
            System.err.println("SQL-Statuscode: " + e.getSQLState());
        }
    }

    private User getUser(String username){
        try {
            String getQuerry = "SELECT * FROM users";
            PreparedStatement stmnt = connection.prepareStatement(getQuerry);
            ResultSet rs = stmnt.executeQuery();
            if(rs==null){
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("User get all gescheitert" + e.getMessage());
        }

        // hier muss noch über die datenbank struktur nachgedacht werden
        return null;

    }
}

