package com.github.wuza8.githubapi.exception;

public class CannotGetUserReposException extends Throwable{
    private final String message;

    public CannotGetUserReposException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
