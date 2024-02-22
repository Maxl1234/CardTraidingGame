package com.example.app.mtcg.controller;

import com.example.app.mtcg.db.DbCom;
import com.example.app.mtcg.entity.Card;
import com.example.app.mtcg.entity.User;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;

import java.util.Vector;

public class CardController extends Controller {
    @Override
    public boolean supports(String route) {
        return route.startsWith("/cards");
    }

    @Override
    public Response handle(Request request) {
        if ( request.getRoute().equals("/cards")){
            switch (request.getMethod()){
                case "GET": return showCards(request);
                default: return status(HttpStatus.METHOD_NOT_ALLOWED);
            }
        }

        Response response = new Response();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setBody("Route: "+request.getRoute()+"\n Methond: "+request.getMethod()+"\n Body: "+request.getBody()
                +"\n Head: "+request.getHost()+"\n Head2: " + request.toString()+"\n Head3: "+request.getAuth());
        return response;
    }
    public boolean checkAuth(String Auth){
        DbCom connection = new DbCom();
        connection.connectdb();
        boolean succ = connection.checkAuth(Auth);
        connection.disconectdb();
        return succ;
    }

    public Response showCards(Request request){
        if(!checkAuth(request.getAuth())){
            return status(HttpStatus.UNAUTHORIZED);
        }
        Response response = new Response();
        response.setContentType(HttpContentType.TEXT_PLAIN);

        DbCom connection = new DbCom();
        User user = connection.getUserByAuth(request.getAuth());
        if(user == null){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setBody("get user failed");
            return response;
        }
        Vector<Card> userCards = new Vector<>();
        userCards = user.getUserCards();
        if(userCards.isEmpty()){
            response.setStatus(HttpStatus.OK);
            response.setBody("User hat noch keine Karten");
            return response;
        }
        String output = "";
        for(Card card : userCards){
            output+= "Name: "+card.getName()+" Type: "+card.getType()+" Element: "+card.getElement()+" Damage: "+card.getDamage()+"\n";
        }
        response.setStatus(HttpStatus.OK);
        response.setBody(output);


        connection.disconectdb();
        return status(HttpStatus.BAD_REQUEST);
    }
}
