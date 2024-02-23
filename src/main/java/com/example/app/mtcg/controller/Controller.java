package com.example.app.mtcg.controller;

import com.example.app.mtcg.db.DbCom;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Request;
import com.example.server.http.Response;

public abstract class Controller {

    public abstract boolean supports(String route);

    public abstract Response handle(Request request);

    protected Response status(HttpStatus httpStatus) {
        Response response = new Response();
        response.setStatus(httpStatus);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("{ \"error\": \"" + httpStatus.getMessage() + "\"}");

        return response;
    }
    public boolean checkAuth(String Auth){
        DbCom connection = new DbCom();
        connection.connectdb();
        boolean succ = connection.checkAuth(Auth);
        connection.disconectdb();
        return succ;
    }
}