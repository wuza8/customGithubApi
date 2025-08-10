package com.github.wuza8.githubapi.controller;

import com.github.wuza8.githubapi.exception.ErrorGithubResponseException;
import com.github.wuza8.githubapi.service.CustomGithubApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
public class CustomGithubApiController {

    public record ErrorDto(int status, String message) {
    }

    private final CustomGithubApiService customGithubApiService;

    public CustomGithubApiController(CustomGithubApiService customGithubApiService) {
        this.customGithubApiService = customGithubApiService;
    }

    @GetMapping("repos/{username}")
    public ResponseEntity<List<CustomGithubApiService.RepositoryInfoDto>> getUserInfo(@PathVariable String username)
            throws ErrorGithubResponseException, HttpClientErrorException {
        var repos = customGithubApiService.getUserPublicRepos(username);
        return ResponseEntity.status(HttpStatus.OK).body(repos);
    }

    @ExceptionHandler(ErrorGithubResponseException.class)
    public ResponseEntity<ErrorDto> handleGithubErrorResponse(ErrorGithubResponseException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(404, ex.getMessage()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDto> handleGithubBadResponse(HttpClientErrorException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(404, ex.getMessage()));
    }
}
