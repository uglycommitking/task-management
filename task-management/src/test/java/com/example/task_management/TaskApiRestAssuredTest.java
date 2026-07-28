package com.example.task_management;

import com.example.task_management.auth.repository.AuthEntity;
import com.example.task_management.auth.repository.AuthRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TaskApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Container
    //@ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("7535");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setup(){
        RestAssured.port = port;
        if(!authRepository.existsByUsername("username")){
            AuthEntity authEntity = new AuthEntity();
            authEntity.setUsername("username");
            authEntity.setPassword(passwordEncoder.encode("password"));
            authRepository.save(authEntity);
        }
    }

    @Test
    void createAndGetTask_fullCycle(){
        LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        int taskId = given()
                .auth().preemptive().basic("username", "password")
                .contentType(ContentType.JSON)
                .body(
                        """
                        {
							"creatorId":10,
							"assignedUserId":1,
							"deadlineDate": "%s",
							"priority": "LOW"
						}
						""".formatted(deadline))
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .body("creatorId", equalTo(10))
                .extract().path("id");

        given()
                .auth().preemptive().basic("username", "password")
                .when()
                .get("/tasks/" + taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("creatorId", equalTo(10))
                .body("priority", equalTo("LOW"));

    }
}
