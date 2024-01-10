package com.esiea.todo.tasks;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TaskEditForm {

    @NotNull
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @ValidStatus
    private String status;

    @NotEmpty
    private String dueDate;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public String getDueDate() {
        return dueDate;
    }

}