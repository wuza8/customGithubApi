package com.github.wuza8.githubapi.service;

import com.github.wuza8.githubapi.dto.GithubApiRepoDto;
import com.github.wuza8.githubapi.dto.GithubApiRepoListDto;
import com.github.wuza8.githubapi.dto.RepositoryInfoDto;
import com.github.wuza8.githubapi.exception.CannotGetUserReposException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomGithubApiService {

    private final RestTemplate restTemplate;

    private record GithubApiBranchesDto(String name, GithubApiCommitDto commit) {}
    private record GithubApiCommitDto(String sha) {}

    public CustomGithubApiService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<RepositoryInfoDto> getUserPublicRepos(String username) throws CannotGetUserReposException {
        String getReposUrlTemplate = "https://api.github.com/search/repositories?q=user:%s";
        GithubApiRepoListDto result = restTemplate.getForObject(String.format(getReposUrlTemplate, username), GithubApiRepoListDto.class);

        if(result == null || result.items == null)
            throw new CannotGetUserReposException("Github API error");

        if(!result.isSuccess())
            throw new CannotGetUserReposException(result.getErrorMessage());

        List<RepositoryInfoDto> output = new ArrayList<>();

        for(GithubApiRepoDto repo : result.items) {
            GithubApiBranchesDto[] branchResult = restTemplate.getForObject(
                    repo.branches_url.replace("{/branch}", ""),
                    GithubApiBranchesDto[].class);

            if (branchResult == null)
                throw new CannotGetUserReposException("Cannot get branches for " + repo.name);

            List<RepositoryInfoDto.BranchDto> branches = new ArrayList<>();

            for(GithubApiBranchesDto branch : branchResult){
                branches.add(new RepositoryInfoDto.BranchDto(branch.name, branch.commit.sha));
            }

            output.add(new RepositoryInfoDto(repo.name, repo.owner.login, branches));
        }

        return output;
    }
}
