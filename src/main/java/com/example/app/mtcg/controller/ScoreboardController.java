package com.example.app.mtcg.controller;

import com.example.app.mtcg.db.DbCom;
import com.example.app.mtcg.entity.Scoreboard;
import com.example.app.mtcg.entity.User;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;

import java.util.Vector;

public class ScoreboardController extends Controller {
    public boolean supports(String route) {
        return route.startsWith("/stats")||route.startsWith("/scoreboard");
    }

    @Override
    public Response handle(Request request) {
        if ( request.getRoute().equals("/stats")){
            switch (request.getMethod()){
                case "GET": return showStats(request);
                default: return status(HttpStatus.METHOD_NOT_ALLOWED);
            }
        }
        if ( request.getRoute().equals("/scoreboard")){
            switch (request.getMethod()){
                case "GET": return showScoreboard(request);
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
    public Response showStats(Request request){
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

        response.setStatus(HttpStatus.OK);
        response.setBody("Username: "+user.getUsername()+" Wins: "+user.getWins()+" Losses: "+user.getLosses()+" Draws: "+user.getDraws()+"\n");
        return response;
    }
    public Response showScoreboard(Request request){
        Response response = new Response();
        response.setContentType(HttpContentType.APPLICATION_JSON);

        if(!checkAuth(request.getAuth())){
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("Not Authorized");
            return response;
        }
        Vector<User> allUsers = new Vector<>();

        DbCom connection = new DbCom();
        connection.connectdb();
        allUsers = connection.getAllUsers();
        Scoreboard scoreboard = new Scoreboard(allUsers);
        response.setStatus(HttpStatus.OK);
        response.setBody(scoreboard.showScores());
        return response;
    }
}
