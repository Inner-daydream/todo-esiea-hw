package com.esiea.todo.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private Date convertDate(String dueDate) {
        Date parsedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            parsedDate = format.parse(dueDate);
        } catch (ParseException e) {
            throw new InvalidDateException(dueDate);
        }
        return parsedDate;
    }

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, String description, String dueDate) {
        Date parsedDate = convertDate(dueDate);
        Task task = new Task(title, description, parsedDate);
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long id, String title, String description, Status status, String dueDate) {
        Date parsedDate = convertDate(dueDate);
        Task taskToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskToUpdate.setTitle(title);
        taskToUpdate.setDescription(description);
        taskToUpdate.setStatus(status);
        taskToUpdate.setDueDate(parsedDate);
        return taskRepository.save(taskToUpdate);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Iterable<Task> getTasksByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

}
