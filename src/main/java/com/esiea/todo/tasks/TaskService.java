package com.esiea.todo.tasks;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, String description, Date dueDate) {
        Task task = new Task(title, description, dueDate);
        Task saved = taskRepository.save(task);
        System.out.println("saved: " + saved.getDueDate());
        return saved;
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long id, String title, String description, Status status, Date dueDate) {
        Task taskToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskToUpdate.setTitle(title);
        taskToUpdate.setDescription(description);
        taskToUpdate.setStatus(status);
        taskToUpdate.setDueDate(dueDate);
        return taskRepository.save(taskToUpdate);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Iterable<Task> getTasksByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

}
