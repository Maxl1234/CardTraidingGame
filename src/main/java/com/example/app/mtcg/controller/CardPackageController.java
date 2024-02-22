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

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class CardPackageController extends Controller {
    @Override
    public boolean supports(String route) {return route.startsWith("/packages") || route.startsWith("/transactions");}

    @Override
    public Response handle(Request request) {

        if ( request.getRoute().equals("/packages")){
            switch (request.getMethod()){
                case "POST": return createPackage(request);
                default: return status(HttpStatus.METHOD_NOT_ALLOWED);
            }
        }
        else if(request.getRoute().startsWith("/transactions")){
            switch (request.getMethod()){
                case "POST": return buyPackage(request);
                default:return status(HttpStatus.METHOD_NOT_ALLOWED);
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
    public Response buyPackage(Request request){
        if(!checkAuth(request.getAuth())){
            return status(HttpStatus.UNAUTHORIZED);
        }
        Response response = new Response();
        response.setContentType(HttpContentType.TEXT_PLAIN);
        DbCom connection = new DbCom();
        connection.connectdb();
        User user = connection.getUserByAuth(request.getAuth());
        if (user == null){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setBody("No User found");
            return response;
        }
        else if(user.getCurrency()<=0){
            response.setStatus(HttpStatus.OK);
            response.setBody("User is out of money");
            return response;
        }

        CardPackage pack = connection.getPackage();
        if(pack.getCardPack().isEmpty()){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setBody("No Package found");
            return response;
        }

        user.addPackage(pack);
        user.updateDeck();
        user.setCurrency(user.getCurrency()-5);

        if(!connection.updateUserBuyPack(user)){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setBody("User Update failed");
            return response;
        }
        connection.deletePackage(pack);
        connection.disconectdb();
        response.setStatus(HttpStatus.OK);
        response.setBody("Package successfully bought");
        return response;
    }

    public boolean checkAdmin(Request request){
        String string = request.getAuth();
        return string.contains("admin-mtcgToken")? true : false;
    }

    public Response createPackage(Request request)  {
        if(!checkAdmin(request)){
            return status(HttpStatus.BAD_REQUEST);
        }
        List<Card> cardsPack = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Response response = new Response();
        response.setContentType(HttpContentType.TEXT_PLAIN);


        try{
            JsonNode jsonNode = objectMapper.readTree(request.getBody());
            DbCom connection = new DbCom();
            connection.connectdb();

            for (JsonNode cardNode : jsonNode) {
                Map<String, Object> map = objectMapper.convertValue(cardNode, Map.class);

                Card card = new Card();

                card.setId((String) map.get("Id"));
                card.setName((String) map.get("Name"));
                card.setDamage((double) map.get("Damage"));
                card.setTypeAndElem();
                cardsPack.add(card);
            }

            boolean succ = connection.insertPackage(cardsPack);
            if(succ){
                response.setStatus(HttpStatus.CREATED);
                response.setBody("Package created");
            }
            else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setBody("Package not created");
            }


        }
        catch (JsonProcessingException e){
            System.err.println("create Package not worked" + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setBody("Package create did not work");
        }


        return response;
    }
}
