package com.example.server.http;

// THOUGHT: Add new relevant status (https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    CREATED(201,"Created"),
    INTERNAL_SERVER_ERROR(500,"Internal Server error");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
