package com.example.task_management;

import com.example.task_management.tasks.controller.TaskController;
import com.example.task_management.tasks.model.Priority;
import com.example.task_management.tasks.model.Task;
import com.example.task_management.tasks.model.TaskStatus;
import com.example.task_management.tasks.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private Task createTask(Long id) {
        return new Task(
                id,
                10L,
                20L,
                TaskStatus.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                Priority.MEDIUM,
                null
        );
    }


    @Test
    void getAllTasks_whenTasksExist_returnsTasks() throws Exception{
        Task task = createTask(1L);
        when(taskService.getAllTasks()).thenReturn(List.of(task));
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllTasks_whenNoTasks_returnsEmptyList() throws Exception{
        when(taskService.getAllTasks()).thenReturn(List.of());
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTaskById_whenTaskExist_returnsTask() throws Exception{
        long id = 1;
        Task task = createTask(id);
        when(taskService.findTaskById(id)).thenReturn(task);
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getTaskById_whenTaskNotExist_returnNotFound() throws Exception{
        when(taskService.findTaskById(1L)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void createTask_whenDataIsValid_returnsCreatedTask() throws Exception{
        Task createdTask = createTask(1L);
        when(taskService.createTask(any())).thenReturn(createdTask);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "creatorId": 10,
                    "assignedUserId": 20,
                    "deadlineDate": "%s",
                    "priority": "MEDIUM"
                }
                """.formatted(deadline)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    void createTask_whenStatusNotNull_returnsBadRequest() throws Exception{

        when(taskService.createTask(any())).thenThrow(IllegalArgumentException.class);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "creatorId": 10,
                    "assignedUserId": 20,
                    "status": "CREATED",
                    "deadlineDate": "%s",
                    "priority": "MEDIUM"
                }
                """.formatted(deadline)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    @Test
    void updateTask_whenDataIsValid_returnsUpdatedTask() throws Exception{
        long id = 1;
        Task task = createTask(1L);
        when(taskService.updateTask(eq(id),any())).thenReturn(task);

        LocalDateTime deadlineTime = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(put("/tasks/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "creatorId": 10,
                            "assignedUserId": 20,
                            "status": "IN_PROGRESS",
                            "deadlineDate": "%s",
                            "priority": "MEDIUM"
                        }
                        """.formatted(deadlineTime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void updateTask_whenTaskNotExists_returnsNotFound() throws Exception{
        long id = 1;
        when(taskService.updateTask(eq(id),any())).thenThrow(EntityNotFoundException.class);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        mockMvc.perform(put("/tasks/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                            "creatorId": 10,
                            "assignedUserId": 20,
                            "status": "IN_PROGRESS",
                            "deadlineDate": "%s",
                            "priority": "MEDIUM"
                        }
                        """.formatted(deadline)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));

    }
    @Test
    void updateTask_whenTaskIsDone_returnsConflict () throws Exception{
        long id = 1;
        when(taskService.updateTask(eq(id),any())).thenThrow(IllegalStateException.class);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "creatorId": 10,
                            "assignedUserId": 20,
                            "status": "IN_PROGRESS",
                            "deadlineDate": "%s",
                            "priority": "MEDIUM"
                        }
                        """.formatted(deadline)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Conflict"));
    }
    @Test
    void updateTask_whenDeadlineInvalid_returnsBadRequest  () throws Exception{
        long id = 1;
        when(taskService.updateTask(eq(id),any())).thenThrow(IllegalArgumentException.class);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "creatorId": 10,
                            "assignedUserId": 20,
                            "status": "IN_PROGRESS",
                            "deadlineDate": "%s",
                            "priority": "MEDIUM"
                        }
                        """.formatted(deadline)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    @Test
    void deleteTaskById_WhenTaskExist_returnsOk() throws Exception{
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());
        verify(taskService).deleteTaskById(1L);
    }

    @Test
    void deleteTaskById_whenTaskNotExists_returnsNotFound() throws Exception{
        doThrow(new EntityNotFoundException("Not found task by id = 1")).when(taskService).deleteTaskById(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"))
                .andExpect(jsonPath("$.detailedMessage").value("Not found task by id = 1"));
    }

    @Test
    void reopenTask_whenTaskExist_returnsTask() throws Exception{
        Task response = createTask(1L);
        when(taskService.reopenTask(1L)).thenReturn(response);
        mockMvc.perform(patch("/tasks/1/reopen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void reopenTask_whenTaskNotExist_returnsNotFound() throws Exception{
        long id = 1;
        when(taskService.reopenTask(id)).thenThrow(new EntityNotFoundException("Not found task by id = " + id));
        mockMvc.perform(patch("/tasks/1/reopen"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"))
                .andExpect(jsonPath("$.detailedMessage").value("Not found task by id = " + id));
    }

    @Test
    void reopenTask_whenIllegalState_returnsBadRequest() throws Exception{
        long id = 1;
        when(taskService.reopenTask(id)).thenThrow(new IllegalArgumentException("Task with id = " + id + " must be DONE to reopen"));
        mockMvc.perform(patch("/tasks/1/reopen"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad request"))
                .andExpect(jsonPath("$.detailedMessage").value("Task with id = " + id + " must be DONE to reopen"));
    }

    @Test
    void startTask_whenTaskExist_returnsTask() throws Exception{
        long id = 1;
        Task task = createTask(id);
        when(taskService.startTask(id)).thenReturn(task);
        mockMvc.perform(post("/tasks/{id}/start",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.creatorId").value(10));
    }

    @Test
    void startTask_whenTaskNotExist_returnsNotFound() throws Exception{
        long id = 1;
        when(taskService.startTask(id)).thenThrow(new EntityNotFoundException("Not found task by id = " + id));
        mockMvc.perform(post("/tasks/{id}/start",id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"))
                .andExpect(jsonPath("$.detailedMessage").value("Not found task by id = " + id));
    }

    @Test
    void startTask_whenCountOfTasksMoreThanFive_returnsBadRequest() throws Exception{
        long id = 1;
        when(taskService.startTask(id)).thenThrow(new IllegalArgumentException("you have a lot of tasks with IN_PROGRESS status"));
        mockMvc.perform(post("/tasks/{id}/start",id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad request"))
                .andExpect(jsonPath("$.detailedMessage").value("you have a lot of tasks with IN_PROGRESS status"));
    }

    @Test
    void completeTask_whenTaskExist_returnsTask() throws Exception{
        long id = 1;
        Task task = createTask(id);
        when(taskService.completeTask(id)).thenReturn(task);
        mockMvc.perform(post("/tasks/{id}/complete",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void completeTask_whenTaskNotExist_returnsNotFound() throws Exception{
        long id = 1;
        when(taskService.completeTask(id)).thenThrow(new EntityNotFoundException("Not found task by id = " + id));
        mockMvc.perform(post("/tasks/{id}/complete",id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"))
                .andExpect(jsonPath("$.detailedMessage").value("Not found task by id = " + id));
    }

    @Test
    void completeTask_whenStatusIsNotInProgress_returnsConflict() throws Exception{
        long id = 1;
        when(taskService.completeTask(id)).thenThrow(new IllegalStateException("Task with id = " + id + " must be IN_PROGRESS to complete"));
        mockMvc.perform(post("/tasks/{id}/complete",id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Conflict"))
                .andExpect(jsonPath("$.detailedMessage").value("Task with id = " + id + " must be IN_PROGRESS to complete"));
    }

















}
