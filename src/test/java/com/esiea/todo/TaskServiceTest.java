package com.esiea.todo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esiea.todo.tasks.Status;
import com.esiea.todo.tasks.Task;
import com.esiea.todo.tasks.TaskNotFoundException;
import com.esiea.todo.tasks.TaskRepository;
import com.esiea.todo.tasks.TaskService;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTask() {
        Date date = new Date();
        Task task = new Task("title", "description", date);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask("title", "description", new Date());

        assertNotNull(result);
        assertEquals("title", result.getTitle());
        assertEquals("description", result.getDescription());
        assertEquals(date, result.getDueDate());
        assertEquals(Status.OPEN, result.getStatus());
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("title1", "description1", new Date());
        Task task2 = new Task("title2", "description2", new Date());
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }

    @Test
    public void testGetTaskById() {

        Long taskId = 1L;
        Long taskIdFail = 2L;

        Date date = new Date();
        Task task = new Task("title", "description", date);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.findById(taskIdFail)).thenReturn(Optional.empty());

        Task result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals("title", result.getTitle());
        assertEquals("description", result.getDescription());
        assertEquals(Status.OPEN, result.getStatus());
        assertEquals(date, result.getDueDate());
        // Assert that a TaskNotFoundException is thrown when trying to get a task with
        // ID 1
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskIdFail));
    }

    @Test
    public void testUpdateTask() {
        Long taskId = 1L;
        Date date = new Date();
        Date modifiedDate = new Date();
        String modifiedTitle = "modifiedTitle";
        String modifiedDescription = "modifiedDescription";
        Task task = new Task("title", "description", date);
        Task modifiedTask = new Task(modifiedTitle, modifiedDescription, modifiedDate);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(modifiedTask);

        Task result = taskService.updateTask(taskId, modifiedTitle, modifiedDescription, Status.OPEN, modifiedDate);

        assertNotNull(result);
        assertEquals(modifiedTitle, result.getTitle());
        assertEquals(modifiedDescription, result.getDescription());
        assertEquals(Status.OPEN, result.getStatus());
        assertEquals(modifiedDate, result.getDueDate());
    }

    @Test
    public void testDeleteTask() {
        Long taskId = 1L;
        doNothing().when(taskRepository).deleteById(taskId);
        taskService.deleteTask(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    public void testGetTasksByStatus() {
        Task task1 = new Task("title1", "description1", new Date());
        Task task2 = new Task("title2", "description2", new Date());
        when(taskRepository.findByStatus(Status.OPEN)).thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> result = taskService.getTasksByStatus(Status.OPEN);

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }

}
