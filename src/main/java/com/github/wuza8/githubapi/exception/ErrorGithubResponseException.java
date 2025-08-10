package com.github.wuza8.githubapi.exception;

public class ErrorGithubResponseException extends RuntimeException {
    public int code;
    private final String message;

    public ErrorGithubResponseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
