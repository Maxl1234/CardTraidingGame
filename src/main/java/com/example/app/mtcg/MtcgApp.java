package com.example.app.mtcg;


import com.example.app.mtcg.controller.CardController;
import com.example.app.mtcg.controller.Controller;
import com.example.app.mtcg.controller.UserController;
import com.example.server.http.HttpContentType;
import com.example.server.http.HttpStatus;
import com.example.server.http.Response;
import com.example.server.ServerApplication;
import com.example.server.http.Request;

import java.util.ArrayList;
import java.util.List;

public class MtcgApp implements ServerApplication{

    private final List<com.example.app.mtcg.controller.Controller> controllers = new ArrayList<>();

    public MtcgApp(){
        controllers.add(new UserController());

    }


    @Override
    public Response handle(Request request) {

        for (Controller controller: controllers) {
            if (!controller.supports(request.getRoute())) {
                continue;
            }

            // THOUGHT: implement this idea
            try {
                return controller.handle(request);
            /*
            // HttpException doesn't exists yet
            } catch (HttpException e) {
                // return e.getHttpStatus() response
            }
            */
            } catch (Exception e) {
                // return 500 Internal Server Error
            }
        }

        Response response = new Response();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("Route not found!");
        return response;
    }
}
