package com.example.task_management;

import com.example.task_management.tasks.mapper.TaskMapper;
import com.example.task_management.tasks.model.Priority;
import com.example.task_management.tasks.model.TaskRequest;
import com.example.task_management.tasks.model.TaskResponse;
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

    private TaskRequest validRequest() {
        return new TaskRequest(1L, 2L, LocalDateTime.now().plusDays(1), Priority.MEDIUM);
    }

    @Test
    void getAllTasks_whenNoTasks_returnsEmptyList(){

        when(taskRepository.findAll()).thenReturn(List.of());

        List<TaskResponse> allTasks = taskService.getAllTasks();

        assertEquals(0, allTasks.size());

    }

    @Test
    void getAllTasks_whenTasksExist_returnsMappedTasks(){

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setStatus(TaskStatus.CREATED);

        TaskResponse task = new TaskResponse(1L, null, null,TaskStatus.CREATED,null,
                null,null,null);


        when(taskRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(task);

        List<TaskResponse> allTasks = taskService.getAllTasks();

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
    void reopenTask_whenStatusNotDone_throwsIllegalState(){
        long id = 1;
        TaskEntity entity = new TaskEntity();
        entity.setId(id);
        entity.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class,
                ()-> taskService.reopenTask(id));
    }

    @Test
    void reopenTask_whenStatusDone_setsStatusToInProgress(){

        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setStatus(TaskStatus.DONE);

        TaskResponse task = new TaskResponse(id,null,null,TaskStatus.DONE,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(mapper.toDomain(taskEntity)).thenReturn(task);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        TaskResponse reopenedTask = taskService.reopenTask(id);

        assertAll(
                ()->assertEquals(task, reopenedTask),
                ()->assertEquals(TaskStatus.IN_PROGRESS, taskEntity.getStatus())
        );
    }

    @Test
    void createTask_savesEntityAndReturnsMappedTask() {
        TaskRequest request = validRequest();

        TaskEntity taskEntity = new TaskEntity();
        TaskResponse expectedResponse = new TaskResponse(1L, 1L, 1L, TaskStatus.CREATED,
                LocalDateTime.now(), request.deadlineDate(), request.priority(), null);

        when(mapper.toEntity(request)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(mapper.toDomain(taskEntity)).thenReturn(expectedResponse);

        TaskResponse result = taskService.createTask(request);

        assertEquals(expectedResponse, result);
        verify(taskRepository).save(taskEntity);
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

        TaskResponse task = new TaskResponse(id, null,null,TaskStatus.IN_PROGRESS,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(task);

        assertEquals(task, taskService.findTaskById(id));
    }

    @Test
    void updateTask_whenTaskNotFound_throwsEntityNotFound(){
        long id = 1;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(id,validRequest()));

    }

    @Test
    void updateTask_whenStatusDone_throwsIllegalState(){
        long id = 1;

        TaskEntity entity = new TaskEntity(1L, 1L, 2L,TaskStatus.DONE,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> taskService.updateTask(id, validRequest()));
    }

    @Test
    void updateTask_whenDeadlineNotAfterCreateDate_throwsIllegalArgument(){

        TaskRequest request = validRequest();

        LocalDateTime createDate = LocalDateTime.now();

        TaskEntity taskEntity = new TaskEntity(null, 2L,null,TaskStatus.IN_PROGRESS,
                createDate.plusDays(2),createDate.plusDays(1),null,null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, request));
    }

    @Test
    void updateTask_whenValidRequest_updatesFieldsAndReturnsTask() {
        long id = 1L;
        LocalDateTime createDate = LocalDateTime.now();

        TaskRequest request = validRequest();

        TaskEntity taskEntity = new TaskEntity(1L, 5L, 7L, TaskStatus.CREATED,
                createDate, createDate.plusDays(1), Priority.LOW, null);

        TaskResponse expectedResponse = new TaskResponse(1L,
                request.creatorId(), request.assignedUserId(), TaskStatus.CREATED,
                createDate, request.deadlineDate(), request.priority(), null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(mapper.toDomain(taskEntity)).thenReturn(expectedResponse);

        TaskResponse result = taskService.updateTask(id, request);

        assertAll(
                () -> assertEquals(expectedResponse, result),
                () -> assertEquals(request.creatorId(), taskEntity.getCreatorId()),
                () -> assertEquals(request.assignedUserId(), taskEntity.getAssignedUserId()),
                () -> assertEquals(request.deadlineDate(), taskEntity.getDeadlineDate()),
                () -> assertEquals(request.priority(), taskEntity.getPriority())
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
    void startTask_whenCountOfTasksMoreThanFive_throwsIllegalState(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setAssignedUserId(id);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.countByAssignedUserIdAndStatus(
                taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS)).thenReturn(5);

        assertThrows(IllegalStateException.class, ()->taskService.startTask(id));
    }

    @Test
    void startTask_whenValidRequest_updatesStatusAndReturnsTask(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setAssignedUserId(id);
        taskEntity.setStatus(TaskStatus.CREATED);

        TaskResponse taskEntityDomain = new TaskResponse(id,null,id,TaskStatus.IN_PROGRESS,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.countByAssignedUserIdAndStatus(
                taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS)).thenReturn(3);
        when(mapper.toDomain(taskEntity)).thenReturn(taskEntityDomain);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        TaskResponse result = taskService.startTask(id);

        assertAll(
                () -> assertEquals(taskEntityDomain, result),
                ()-> assertEquals(TaskStatus.IN_PROGRESS, taskEntity.getStatus())
        );
    }

    @Test
    void completeTask_whenTaskNotFound_throwsEntityNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->taskService.completeTask(1L));
    }

    @Test
    void completeTask_whenStatusNotInProgress_throwsIllegalState(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setStatus(TaskStatus.CREATED);
        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));

        assertThrows(IllegalStateException.class, ()->taskService.completeTask(id));
    }

    @Test
    void completeTask_whenValidRequest_setStatusAndDoneDateTime(){
        long id = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);

        TaskResponse taskEntityDomain = new TaskResponse(id,null,null,TaskStatus.DONE,
                null,null,null,null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(mapper.toDomain(taskEntity)).thenReturn(taskEntityDomain);

        TaskResponse result = taskService.completeTask(id);

        assertAll(
                ()->assertEquals(taskEntityDomain,result),
                ()->assertEquals(TaskStatus.DONE,taskEntity.getStatus()),
                ()->assertNotNull(taskEntity.getDoneDateTime())
        );

    }

}













