package com.example.app.mtcg.controller;

import com.example.server.http.Request;
import com.example.server.http.Response;

public class CardController extends Controller {
    @Override
    public boolean supports(String route) {
        return false;
    }

    @Override
    public Response handle(Request request) {
        return null;
    }
}
