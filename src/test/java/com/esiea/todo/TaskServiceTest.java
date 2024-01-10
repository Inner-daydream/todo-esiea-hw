package com.esiea.todo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esiea.todo.tasks.InvalidDateException;
import com.esiea.todo.tasks.Priority;
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
    public void testInvalidDate() {
        assertThrows(InvalidDateException.class,
                () -> taskService.createTask("title", "description", "invalidDate", Priority.NORMAL));
        assertThrows(InvalidDateException.class,
                () -> taskService.updateTask(1L, "title", "description", Status.OPEN, "invalidDate", Priority.NORMAL));
    }

    @Test
    public void testCreateTask() {
        Date date = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(date);
        Task task = new Task("title", "description", date, Priority.NORMAL);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask("title", "description", formattedDate, Priority.NORMAL);

        assertNotNull(result);
        assertEquals("title", result.getTitle());
        assertEquals("description", result.getDescription());
        assertEquals(date, result.getDueDate());
        assertEquals(Status.OPEN, result.getStatus());
        assertEquals(Priority.NORMAL, result.getPriority());
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("title1", "description1", new Date(), Priority.NORMAL);
        Task task2 = new Task("title2", "description2", new Date(), Priority.NORMAL);
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
        Task task = new Task("title", "description", date, Priority.NORMAL);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.findById(taskIdFail)).thenReturn(Optional.empty());

        Task result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals("title", result.getTitle());
        assertEquals("description", result.getDescription());
        assertEquals(Status.OPEN, result.getStatus());
        assertEquals(date, result.getDueDate());
        assertEquals(Priority.NORMAL, result.getPriority());
        // Assert that a TaskNotFoundException is thrown when trying to get a task with
        // ID 1
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskIdFail));
    }

    @Test
    public void testUpdateTask() {
        Long taskId = 1L;
        Date date = new Date();
        Date modifiedDate = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String modifiedFormattedDate = formatter.format(modifiedDate);
        String modifiedTitle = "modifiedTitle";
        String modifiedDescription = "modifiedDescription";
        Task task = new Task("title", "description", date, Priority.NORMAL);
        Task modifiedTask = new Task(modifiedTitle, modifiedDescription, modifiedDate, Priority.HIGH);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(modifiedTask);

        Task result = taskService.updateTask(taskId, modifiedTitle, modifiedDescription, Status.OPEN,
                modifiedFormattedDate, Priority.HIGH);

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
        Task task1 = new Task("title1", "description1", new Date(), Priority.NORMAL);
        Task task2 = new Task("title2", "description2", new Date(), Priority.NORMAL);
        when(taskRepository.findByStatus(Status.OPEN)).thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> result = taskService.getTasksByStatus(Status.OPEN);

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());

    }

    @Test
    public void testUpdateNonExistingTask() {
        Long taskId = 1L;
        String date = "2023-01-01";
        String title = "title";
        String description = "description";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, title, description, Status.OPEN,
                date, Priority.NORMAL));

    }

    @Test
    public void testGetTasksByStatusAndPriorityDesc() {
        Task task1 = new Task("title1", "description1", new Date(), Priority.HIGH);
        Task task2 = new Task("title2", "description2", new Date(), Priority.NORMAL);
        when(taskRepository.findByStatusOrderByPriorityDesc(Status.OPEN)).thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> result = taskService.getTasksByStatusAndPriorityDesc(Status.OPEN);

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }

    @Test
    public void testGetAllTasksSortedByPriorityDesc() {
        Task task1 = new Task("title1", "description1", new Date(), Priority.HIGH);
        Task task2 = new Task("title2", "description2", new Date(), Priority.NORMAL);
        when(taskRepository.findAllByOrderByPriorityDesc()).thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> result = taskService.getAllTasksSortedByPriorityDesc();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }
}
