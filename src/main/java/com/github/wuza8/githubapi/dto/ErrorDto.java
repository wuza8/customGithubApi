package com.github.wuza8.githubapi.dto;

public class ErrorDto {
    public ErrorDto(int status, String message){
        this.status = status;
        this.message = message;
    }

    public int status;
    public String message;
}
