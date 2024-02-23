package com.example.app.mtcg.controller;

import com.example.app.mtcg.db.DbCom;
import com.example.app.mtcg.entity.*;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Vector;

public class DeckController extends Controller {
    @Override
    public boolean supports(String route) {return route.startsWith("/deck"); }

    @Override
    public Response handle(Request request) {
        if ( request.getRoute().startsWith("/deck")){
            switch (request.getMethod()){
                case "GET": return showDeck(request);
                case "PUT": return setDeck(request);
                default: return status(HttpStatus.METHOD_NOT_ALLOWED);
            }
        }
        Response response = new Response();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("Route: "+request.getRoute()+"\n Methond: "+request.getMethod()+"\n Body: "+request.getBody()
                +"\n Head: "+request.getHost()+"\n Head2: " + request.toString()+"\n Head3: "+request.getAuth());
        return response;
    }
    public Response setDeck(Request request) {
        Response response = new Response();
        if(request.getRoute().contains("?format=plain"))
            response.setContentType(HttpContentType.TEXT_PLAIN);
        else
            response.setContentType(HttpContentType.APPLICATION_JSON);

        if(!checkAuth(request.getAuth())){
            response.setStatus(HttpStatus.UNAUTHORIZED);
            return response;
        }
        DbCom connection = new DbCom();
        connection.connectdb();

        User user = connection.getUserByAuth(request.getAuth());
        ObjectMapper objectMapper= new ObjectMapper();
        Vector<String> cardIds = new Vector<String>();


        try {
            JsonNode jsonNode = objectMapper.readTree(request.getBody());

            // Überprüfen, ob es sich um ein JSON-Array handelt
            if (jsonNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) jsonNode;

                // Durchlaufe das JSON-Array und füge die Werte zum Vektor hinzu
                for (JsonNode node : arrayNode) {
                    cardIds.add(node.asText());
                }

                // Hier kannst du cardIds für weitere Verarbeitung verwenden
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setBody("Ungültiges JSON-Array");
                return response;
            }
        } catch (JsonProcessingException e) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("Fehler beim Verarbeiten des JSON-Objekts: " + e.getMessage());
            return response;
        }

        if(cardIds.size() != 4){
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("Zu wenig Karten");
            return response;
        }
        boolean owns = true;


        for (String cardId : cardIds) {
            boolean idMatch = false;

            for (Card card : user.getUserCards()) {
                if (cardId.equals(card.getId())) {
                    idMatch = true;
                    break;
                }
            }

            if (!idMatch) {
                owns = false;
                break;
            }
        }
        if(!owns){
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody(" User besitzt diese Karten nicht ");
            return response;
        }

        if(!connection.replaceDeck(cardIds,user.getUserDeck().getId())){

        }
        response.setStatus(HttpStatus.OK);
        response.setBody(" Deck successfully configured ");
        connection.disconectdb();
        return response;
    }

    public Response showDeck(Request request){
        Response response = new Response();
        response.setContentType(HttpContentType.APPLICATION_JSON);

        if(!checkAuth(request.getAuth())){
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("Not Authorized");
            return response;
        }


        DbCom connection = new DbCom();
        connection.connectdb();
        User user = connection.getUserByAuth(request.getAuth());
        if(user == null){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setBody("get user failed");
            return response;
        }


        Deck userDeck = user.getUserDeck();
        if(userDeck.getDeck().isEmpty()){
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("User has no Deck yet");
        }
        Vector<Card> userCardsDecks = userDeck.getDeck();

        if(userCardsDecks.isEmpty()){
            response.setStatus(HttpStatus.OK);
            response.setBody("User hat noch kein Deck");
            return response;
        }
        String output = "";
        for(Card card : userCardsDecks){
            output+= "Name: "+card.getName()+" Type: "+card.getType()+" Element: "+card.getElement()+" Damage: "+card.getDamage()+"\n";
        }
        response.setStatus(HttpStatus.OK);
        response.setBody(output);


        connection.disconectdb();
        return response;
    }
}
