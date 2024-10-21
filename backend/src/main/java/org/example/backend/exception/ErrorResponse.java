package org.example.backend.exception;

public class ErrorResponse {
    private String error;
    private String message;

    // Constructors
    public ErrorResponse() {}

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    // Getters and Setters

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}