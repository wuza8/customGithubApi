package com.github.wuza8.githubapi.controller;

import com.github.wuza8.githubapi.dto.ErrorDto;
import com.github.wuza8.githubapi.dto.RepositoryInfoDto;
import com.github.wuza8.githubapi.exception.CannotGetUserReposException;
import com.github.wuza8.githubapi.service.CustomGithubApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomGithubApiController {

    private final CustomGithubApiService customGithubApiService;

    public CustomGithubApiController(CustomGithubApiService customGithubApiService) {
        this.customGithubApiService = customGithubApiService;
    }

    @GetMapping("repos/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        try {
            List<RepositoryInfoDto> repos = customGithubApiService.getUserPublicRepos(username);
            return ResponseEntity.status(HttpStatus.OK).body(repos);
        }
        catch(CannotGetUserReposException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(404, ex.getMessage()));
        }
    }
}
