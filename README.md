# CustomGithubAPI
This project is a recruitment task implementing a simple Spring Boot REST API endpoint that retrieves public repositories for a given GitHub username, along with their branches and latest commit SHAs.

## Technologies Used

- Java 17+
- Spring Boot 3.5+
- Gradle
- JUnit (via `spring-boot-starter-test`)  

## Endpoint

**GET** `/repos/{username}`

Retrieves the list of public repositories for a given GitHub username with:
- Repository name
- Owner login
- List of branches (branch name + last commit SHA)

### Example Response
```json
[
  {
    "repositoryName": "my-repo",
    "ownerLogin": "johnsmith",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "a1b2c3d4..."
      }
    ]
  }
]
```

## How to Run
1. Build the application

```
./gradlew build
```

2. Run the application
   
```
./gradlew bootRun
```

The API will be available at:

```
http://localhost:8080
```

## Running Tests

To run all tests:
```
./gradlew test
```

This includes the integration test that verifies the main API endpoint in a "happy path" scenario.

## Running Tests

To run all tests:
```
./gradlew test
```
This includes the integration test that verifies the main API endpoint in a "happy path" scenario.
