package com.example.task_management;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class TaskManagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Container
	//@ServiceConnection
	static PostgreSQLContainer<?>postgres = new PostgreSQLContainer<>("postgres:16")
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

	@Test
	void createAndGetTask_fullCycle() throws Exception {
		LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
		String response = mockMvc.perform(post("/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"creatorId":1,
							"assignedUserId":1,
							"deadlineDate": "%s",
							"priority": "LOW"
						}
						""".formatted(deadline)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.creatorId").value(1))
				.andReturn().getResponse().getContentAsString();

		long id = JsonPath.parse(response).read("$.id", Long.class);

		mockMvc.perform(get("/tasks/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.priority").value("LOW"))
				.andExpect(jsonPath("$.status").value("CREATED"));
	}

}
