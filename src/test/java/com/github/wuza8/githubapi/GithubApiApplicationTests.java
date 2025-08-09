package com.github.wuza8.githubapi;

import com.github.wuza8.githubapi.dto.RepositoryInfoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubApiApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void givenValidUser_whenGetUserRepos_thenReturnRepoList() {
		//given
		String username = "wuza8";

		//when
		ResponseEntity<RepositoryInfoDto[]> response = restTemplate.getForEntity("/repos/"+username, RepositoryInfoDto[].class);

		//then
		String repo_name = "dyscypliner-server";
		String branch_name = "master";
		String last_commit_sha = "a8ce5abb01fa12ec067bac16b8b791e9768f86da";

		Optional<RepositoryInfoDto> found = Arrays.stream(response.getBody())
				.filter(t -> t.repo.equals(repo_name))
				.findFirst();

        Assertions.assertTrue(found.isPresent());
		Assertions.assertEquals(branch_name, found.get().branches.get(0).name());
		Assertions.assertEquals(last_commit_sha, found.get().branches.get(0).last_commit_sha());
	}

}
