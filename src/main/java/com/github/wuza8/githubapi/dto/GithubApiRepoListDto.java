package com.github.wuza8.githubapi.dto;

import java.util.List;

public class GithubApiRepoListDto {

    public record GithubApiErrorDto(String message){}

    public List<GithubApiRepoDto> items;
    public List<GithubApiErrorDto> errors;

    public boolean isSuccess(){
        return errors == null;
    }

    public String getErrorMessage(){
        if(errors == null || errors.isEmpty())
            return "Unknown Github API error";
        else
            return errors.getFirst().message;
    }
}
