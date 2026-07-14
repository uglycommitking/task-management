package com.example.task_management;

import com.example.task_management.tasks.controller.TaskController;
import com.example.task_management.tasks.model.Priority;
import com.example.task_management.tasks.model.Task;
import com.example.task_management.tasks.model.TaskStatus;
import com.example.task_management.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

}
