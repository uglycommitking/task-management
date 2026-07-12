package com.example.task_management;

import com.example.task_management.tasks.mapper.TaskMapper;
import com.example.task_management.tasks.model.Task;
import com.example.task_management.tasks.model.TaskStatus;
import com.example.task_management.tasks.repository.TaskEntity;
import com.example.task_management.tasks.repository.TaskRepository;
import com.example.task_management.tasks.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper mapper;
    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldReturnEmptyListWhenNoTasksExist(){

        when(taskRepository.findAll()).thenReturn(List.of());

        List<Task> allTasks = taskService.getAllTasks();

        assertEquals(0, allTasks.size());

    }

    @Test
    void shouldReturnNotEmptyListWhenTasksExist(){

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setStatus(TaskStatus.CREATED);

        Task task = new Task(1L, null, null,TaskStatus.CREATED,null,
                null,null,null);


        when(taskRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(task);

        List<Task> allTasks = taskService.getAllTasks();

        assertAll(
                () -> assertEquals(1, allTasks.size(), "Количество задач в списке не совпадает"),
                () -> assertEquals(1L, allTasks.get(0).id(), "ID первой задачи не совпадает"),
                () -> assertEquals(TaskStatus.CREATED, allTasks.get(0).status(), "Статус первой задачи не совпадает")
        );
    }

    @Test
    void shouldReturnEntityNotFoundWhenReopenTaskNotFound(){
        long id = 1;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> taskService.reopenTask(id));
    }

    @Test
    void shouldReturnIllegalArgumentWhenStatusNotDone(){
        long id = 1;
        TaskEntity entity = new TaskEntity();
        entity.setId(id);
        entity.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalArgumentException.class,
                ()-> taskService.reopenTask(id));
    }

    @Test
    void shouldReturnTaskAfterReopen(){

        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setStatus(TaskStatus.DONE);

        Task task = new Task(id,null,null,TaskStatus.DONE,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(mapper.toDomain(taskEntity)).thenReturn(task);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        Task reopenedTask = taskService.reopenTask(id);

        assertEquals(task, reopenedTask);

    }

}
