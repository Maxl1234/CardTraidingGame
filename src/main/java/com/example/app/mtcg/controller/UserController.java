package com.example.app.mtcg.controller;

import com.example.app.mtcg.entity.*;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;
import com.example.app.mtcg.db.DbCom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class UserController extends Controller {

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users") || route.startsWith("/sessions");
    }

    @Override
    public Response handle(Request request) {


        if ( request.getRoute().equals("/users")){
            switch (request.getMethod()){
                case "GET": return listUsers(request);
                case "POST": return createUser(request);
                case "PUT": return updateUser(request);
                default: return status(HttpStatus.METHOD_NOT_ALLOWED);


            }

        }
        else if ( request.getRoute().equals("/sessions")){
            switch (request.getMethod()){
                case "POST" : return loginUser(request);
                default:return status(HttpStatus.METHOD_NOT_ALLOWED);
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
    public Response loginUser(Request request) {
        String username, password;
        Response response = new Response();
        response.setContentType(HttpContentType.APPLICATION_JSON);


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(request.getBody());
            Map<String, Object> map = objectMapper.convertValue(jsonNode, Map.class);
            username = (String) map.get("Username");
            password = (String) map.get("Password");
            DbCom connection = new DbCom();
            connection.connectdb();
            User loginUser = connection.getUser(username);

            if (loginUser != null && loginUser.getPassword().equals(password)) {
                if (connection.insertToken(loginUser)) {
                    response.setStatus(HttpStatus.CREATED);
                    response.setBody("Token created");
                }
                else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setBody("Failed to create Token");
                }

            }
            else {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setBody("Wrong Credentials");
            }
        }
        catch(JsonProcessingException e){
            System.err.println(e.getMessage());
        }
        return response;
    }



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
        Boolean succ = connection.insertUser(newUser);

        String userJson = null;
        try {
            userJson = objectMapper.writeValueAsString(newUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response = new Response();
        response.setContentType(HttpContentType.APPLICATION_JSON);
        if(succ){
            response.setStatus(HttpStatus.CREATED);
            response.setBody("User created");
        }
        else {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("User creation failed");
        }

        return response;
    };
    public Response updateUser(Request request){
        return status(HttpStatus.BAD_REQUEST);
    }
}
