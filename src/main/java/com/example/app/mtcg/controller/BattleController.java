package com.example.app.mtcg.controller;

import com.example.app.mtcg.db.DbCom;
import com.example.app.mtcg.entity.Battle;
import com.example.app.mtcg.entity.User;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;

public class BattleController extends Controller{
    private User waitingUser;

    @Override
    public boolean supports(String route) {
        return route.startsWith("/battles");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/battles")) {
            switch (request.getMethod()) {
                case "POST":
                    return startBattle(request);
                default:
                    return status(HttpStatus.METHOD_NOT_ALLOWED);
            }
        }
        Response response = new Response();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("Route: "+request.getRoute()+"\n Methond: "+request.getMethod()+"\n Body: "+request.getBody()
                +"\n Head: "+request.getHost()+"\n Head2: " + request.toString()+"\n Head3: "+request.getAuth());
        return response;
    }

    public Response startBattle(Request request){
        Response response = new Response();
        response.setContentType(HttpContentType.APPLICATION_JSON);

        if(!checkAuth(request.getAuth())){
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("Not Authorized");
            return response;
        }
        User user;

        DbCom connection = new DbCom();
        connection.connectdb();
        user = connection.getUserByAuth(request.getAuth());

        if (waitingUser == null) {
            waitingUser = user;

            // Warte auf den zweiten Benutzer (UserB)
            return createWaitingResponse();
        } else {
            // Wenn der erste Benutzer bereits vorhanden ist, setze ihn als UserA und den aktuellen Benutzer als UserB
            User userA = waitingUser;
            User userB = user;

            // Setze den Wartezustand zurück
            waitingUser = null;

            // Führe den Kampf durch
            Battle battle = new Battle(userA, userB);
            String battleLog = battle.startBattle();
            if(!connection.updateUserBattle(userA) || !connection.updateUserBattle(userB)){
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setBody("update failure");
                return response;
            }

            // Erstelle die Antwort mit dem Kampfprotokoll

            response.setStatus(HttpStatus.OK);
            response.setBody(battleLog);
            connection.disconectdb();
            return response;
        }
    }

    private Response createWaitingResponse() {
        Response response = new Response();
        response.setStatus(HttpStatus.ACCEPTED);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setBody("Waiting for second player...");
        return response;
    }
}
