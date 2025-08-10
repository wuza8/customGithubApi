package com.github.wuza8.githubapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.wuza8.githubapi.service.CustomGithubApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
@SpringBootTest
@AutoConfigureMockMvc
class GithubApiApplicationTests {

    WireMockServer wireMockServer = new WireMockServer(8089); // port testowy

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestRestClientConfig {
        @Bean
        RestClient restClient(RestClient.Builder builder) {
            return builder.baseUrl("http://localhost:8089").build();
        }
    }

    @BeforeEach
    void setup() {
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @Test
    void givenValidUser_whenGetUserRepos_thenReturnRepoList() throws Exception {
        //given
        String username = "testuser";
        String repo_name = "testapp1";

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/search/repositories"))
                .withQueryParam("q", equalTo("user:testuser"))
                .willReturn(WireMock.okJson("""
                        {
                        	"total_count": 2,
                        	"incomplete_results": false,
                        	"items": [
                        		{
                        			"name": "testapp1",
                        			"private": false,
                        			"owner": {
                        				"login": "testuser"
                        			},
                        			"fork": false,
                        			"branches_url": "https://api.github.com/repos/testuser/testapp1/branches{/branch}"
                        		},
                        		{
                        			"name": "testapp2",
                        			"private": false,
                        			"owner": {
                        				"login": "testuser"
                        			},
                        			"fork": true,
                        			"branches_url": "https://api.github.com/repos/testuser/testapp2/branches{/branch}"
                        		}
                        	]
                        }"""
                ))
        );

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(String.format("/repos/%s/%s/branches", username, repo_name)))
                .willReturn(WireMock.okJson("""
                        [
                          {
                        	"name": "master",
                        	"commit": {
                        	  "sha": "c5d3dc173ee726c73ea79d01586f713cdd93a131"
                        	}
                          }
                        ]"""
                ))
        );

        //when
        var jsonResponse = mockMvc.perform(get("/repos/{username}", username))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var result = objectMapper.readValue(jsonResponse, CustomGithubApiService.RepositoryInfoDto[].class);

        //then

        String branch_name = "master";
        String last_commit_sha = "c5d3dc173ee726c73ea79d01586f713cdd93a131";

        Assertions.assertNotNull(result);

        //Was the fork skipped?
        Assertions.assertEquals(1, result.length);

        Optional<CustomGithubApiService.RepositoryInfoDto> found = Arrays.stream(result)
                .filter(t -> t.repo().equals(repo_name))
                .findFirst();

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(branch_name, found.get().branches().getFirst().name());
        Assertions.assertEquals(last_commit_sha, found.get().branches().getFirst().last_commit_sha());
    }
}
