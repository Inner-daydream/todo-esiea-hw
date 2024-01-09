package com.esiea.todo.tasks;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException(String dueDate) {
        super("Invalid date format: " + dueDate);
    }

}
