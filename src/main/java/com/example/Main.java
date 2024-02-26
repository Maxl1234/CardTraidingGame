package com.example;



import com.example.app.mtcg.MtcgApp;

import com.example.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(new MtcgApp());
        server.start();
    }
}

