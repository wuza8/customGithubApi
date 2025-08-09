package com.github.wuza8.githubapi.dto;

import java.util.List;

public class RepositoryInfoDto {
    public record BranchDto(String name, String last_commit_sha) {}

    public RepositoryInfoDto(String repo, String owner, List<BranchDto> branches){
        this.repo = repo;
        this.owner = owner;
        this.branches = branches;
    }
    public String repo;
    public String owner;
    public List<BranchDto> branches;
}
