package com.example.app.mtcg.controller;

import com.example.app.mtcg.entity.*;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;
import com.example.app.mtcg.db.DbCom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class UserController extends Controller {

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {


        if ( request.getRoute().equals("/users")){
            switch (request.getMethod()){
                case "GET": return listUsers(request);
                case "Post": return createUser(request);
                case "PUT": return updateUser(request);
                default: return status(HttpStatus.METHOD_NOT_ALLOWED);


            }

        }
        Response response = new Response();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setBody("Route: "+request.getRoute()+" Methond: "+request.getMethod()+" Body: "+request.getBody());
        return response;
        //return status(HttpStatus.BAD_REQUEST);
    };

    public Response listUsers(Request request){

        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setBody("lists users ()");
        return response;


        //return status(HttpStatus.BAD_REQUEST);
    };
    public Response createUser(Request request){

        ObjectMapper objectMapper = new ObjectMapper();
        User newUser = null;
        String username, password;


        try {
            JsonNode jsonNode = objectMapper.readTree(request.getBody());
            Map<String, Object> map = objectMapper.convertValue(jsonNode, Map.class);
            username = (String) map.get("Username");
            password = (String) map.get("Password");
            newUser = new User(username, password);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace(); // Hier wird die Exception ausgegeben
            throw new RuntimeException(e);
        }


        DbCom connection = new DbCom();
        connection.connectdb();
        connection.insertUser(newUser);

        String userJson = null;
        try {
            userJson = objectMapper.writeValueAsString(newUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        Response response = new Response();
        response.setStatus(HttpStatus.CREATED);
        response.setContentType(HttpContentType.APPLICATION_JSON);

        return response;
    };
    public Response updateUser(Request request){
        return status(HttpStatus.BAD_REQUEST);
    }
}
