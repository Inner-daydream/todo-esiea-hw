package com.esiea.todo.tasks;

public class TaskNotFoundException extends RuntimeException {
    TaskNotFoundException(Long id) {
        super("Could not find task " + id);
    }
}
