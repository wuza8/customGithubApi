package com.github.wuza8.githubapi.service;

import com.github.wuza8.githubapi.exception.ErrorGithubResponseException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomGithubApiService {

    private final RestClient restClient;

    private record GithubApiBranchesDto(String name, GithubApiCommitDto commit) { }

    private record GithubApiCommitDto(String sha) { }

    public record GithubApiErrorDto(String message) { }

    public record GithubApiRepoListDto(List<GithubApiRepoDto> items, List<GithubApiErrorDto> errors) { }

    public record GithubApiRepoDto(String name, GithubApiOwnerDto owner, boolean fork, String branches_url) { }

    public record GithubApiOwnerDto(String login) { }

    public record BranchDto(String name, String last_commit_sha) { }

    public record RepositoryInfoDto(String repo, String owner, List<BranchDto> branches) { }

    public CustomGithubApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<RepositoryInfoDto> getUserPublicRepos(String username) {
        var response = restClient.get()
                .uri("/search/repositories?q=user:{username}", username)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (req, res) -> {
                            throw new ErrorGithubResponseException(res.getStatusCode().value(),
                                    new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8));
                        }
                )
                .toEntity(GithubApiRepoListDto.class);

        return response.getBody().items().stream()
                .filter(s -> !s.fork)
                .map(repo -> {
                    var branchesUrl = repo.branches_url
                            .replace("{/branch}", "")
                            .replace("https://api.github.com", "");

                    GithubApiBranchesDto[] branchResult = restClient.get()
                            .uri(branchesUrl)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (req, res) -> {
                                throw new ErrorGithubResponseException(res.getStatusCode().value(),
                                        String.format("Cannot get branches for %s. Error message: %s",
                                                repo.name,
                                                new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8)
                                        ));
                            })
                            .body(GithubApiBranchesDto[].class);

                    List<BranchDto> branches = Arrays.stream(branchResult)
                            .map(branch -> new BranchDto(branch.name, branch.commit.sha))
                            .toList(); // Java 16+

                    return new RepositoryInfoDto(repo.name, repo.owner.login, branches);
                })
                .toList();
    }
}
