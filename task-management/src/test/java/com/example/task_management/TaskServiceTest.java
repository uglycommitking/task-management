package com.example.task_management;

import com.example.task_management.tasks.mapper.TaskMapper;
import com.example.task_management.tasks.model.Priority;
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
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
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
    void getAllTasks_whenNoTasks_returnsEmptyList(){

        when(taskRepository.findAll()).thenReturn(List.of());

        List<Task> allTasks = taskService.getAllTasks();

        assertEquals(0, allTasks.size());

    }

    @Test
    void getAllTasks_whenTasksExist_returnsMappedTasks(){

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
    void reopenTask_whenTaskNotFound_throwsEntityNotFound(){
        long id = 1;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> taskService.reopenTask(id));
    }

    @Test
    void reopenTask_whenStatusNotDone_throwsIllegalArgument(){
        long id = 1;
        TaskEntity entity = new TaskEntity();
        entity.setId(id);
        entity.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalArgumentException.class,
                ()-> taskService.reopenTask(id));
    }

    @Test
    void reopenTask_whenStatusDone_setsStatusToCreated(){

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

    @Test
    void createTask_whenStatusNotEmpty_throwsIllegalArgument(){
        Task taskToCreate = new Task(1L, null,null,TaskStatus.CREATED,
                null,null,null,null);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskToCreate));
    }

    @Test
    void createTask_whenDeadLineIsAfter_throwsIllegalArgument(){
        Task taskToCreate = new Task(1L, null,null,null,
                null, LocalDateTime.now(),null,null);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskToCreate));
    }

    @Test
    void createTask_ReturnsMappedTask(){
        Task taskToCreate = new Task(1L, null,null,null,
                null, LocalDateTime.now().plusDays(2),null,null);
        Task taskToOutput = new Task(1L, null,null,TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2),null,null);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setDeadlineDate(LocalDateTime.now().plusDays(2));

        when(mapper.toEntity(taskToCreate)).thenReturn(taskEntity);
        when(mapper.toDomain(taskEntity)).thenReturn(taskToOutput);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        Task createdTask = taskService.createTask(taskToCreate);

        assertAll(
                () -> assertEquals(taskToOutput, createdTask),
                () -> assertEquals(TaskStatus.IN_PROGRESS, taskEntity.getStatus()),
                () -> assertNotNull(taskEntity.getCreateDateTime())
        );

    }

    @Test
    void findTaskById_whenTaskNotFound_throwsEntityNotFound(){

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,()-> taskService.findTaskById(1L));
    }

    @Test
    void findTaskById_whenTaskFound_returnsTask(){
        long id = 1;

        TaskEntity entity = new TaskEntity();
        entity.setId(id);
        entity.setStatus(TaskStatus.IN_PROGRESS);

        Task task = new Task(id, null,null,TaskStatus.IN_PROGRESS,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(task);

        assertEquals(task, taskService.findTaskById(id));
    }

    @Test
    void updateTask_whenTaskNotFound_throwsEntityNotFound(){
        long id = 1;
        Task taskToUpdate = new Task(null, 2L,null,null,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(id,taskToUpdate));

    }

    @Test
    void updateTask_whenStatusDone_throwsIllegalState(){
        long id = 1;

        Task taskToUpdate = new Task(null, 2L,null,null,
                null,null,null,null);

        TaskEntity entity = new TaskEntity(1L, 1L, 2L,TaskStatus.DONE,
                null,null,null,null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> taskService.updateTask(id, taskToUpdate));
    }

    @Test
    void updateTask_whenDeadlineNotAfterCreateDate_throwsIllegalArgument(){

        LocalDateTime createDate = LocalDateTime.now();
        Task taskToUpdate = new Task(null, 2L,null,TaskStatus.IN_PROGRESS,
                null,createDate.minusDays(1),null,null);
        TaskEntity taskEntity = new TaskEntity(null, 2L,null,TaskStatus.IN_PROGRESS,
                createDate,null,null,null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, taskToUpdate));
    }

    @Test
    void updateTask_whenValidRequest_updatesFieldsAndReturnsTask(){
        long id = 1;
        LocalDateTime createDate = LocalDateTime.now();

        Task taskToUpdate = new Task(null, 2L, 3L, TaskStatus.IN_PROGRESS,
                null, createDate.plusDays(2), Priority.HIGH, null);

        TaskEntity taskEntity = new TaskEntity(1L, 5L, 7L, TaskStatus.IN_PROGRESS,
                createDate, createDate.plusDays(1), Priority.LOW, null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(mapper.toDomain(taskEntity)).thenReturn(taskToUpdate);

        Task result = taskService.updateTask(id, taskToUpdate);

        assertAll(
                () -> assertEquals(taskToUpdate, result),
                () -> assertEquals(taskToUpdate.creatorId(), taskEntity.getCreatorId()),
                () -> assertEquals(taskToUpdate.assignedUserId(), taskEntity.getAssignedUserId()),
                () -> assertEquals(taskToUpdate.deadlineDate(), taskEntity.getDeadlineDate()),
                () -> assertEquals(taskToUpdate.priority(), taskEntity.getPriority())
        );
    }

    @Test
    void deleteTaskById_whenTaskNotFound_throwsEntityNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->taskService.deleteTaskById(1L));
    }

    @Test
    void deleteTaskById_whenTaskExists_deletesTask(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));

        taskService.deleteTaskById(id);

        verify(taskRepository).delete(taskEntity);
    }

    @Test
    void startTask_whenTaskNotFound_throwsEntityNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->taskService.startTask(1L));
    }

    @Test
    void startTask_whenCountOfTasksMoreThenFive_throwsIllegalArgument(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setAssignedUserId(id);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.countByAssignedUserIdAndStatus(
                taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS)).thenReturn(6);

        assertThrows(IllegalArgumentException.class, ()->taskService.startTask(id));
    }

    @Test
    void startTask_whenValidRequest_updatesStatusAndReturnsTask(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setAssignedUserId(id);
        taskEntity.setStatus(TaskStatus.CREATED);

        Task taskEntityDomain = new Task(id,null,id,TaskStatus.IN_PROGRESS,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.countByAssignedUserIdAndStatus(
                taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS)).thenReturn(3);
        when(mapper.toDomain(taskEntity)).thenReturn(taskEntityDomain);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        Task result = taskService.startTask(id);

        assertAll(
                () -> assertEquals(taskEntityDomain, result),
                ()-> assertEquals(TaskStatus.IN_PROGRESS, taskEntity.getStatus())
        );
    }



}













