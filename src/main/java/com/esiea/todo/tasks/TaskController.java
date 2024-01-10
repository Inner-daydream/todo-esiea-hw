package com.esiea.todo.tasks;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Controller
@Validated
public class TaskController {

    @Autowired
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Provides a default error message to the view when the validation fails via
    // the annotation (eg. @NotEmpty, @NotNull)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException e,
            RedirectAttributes redirectAttributes) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString().replaceAll(".*\\.", ""))
                .collect(Collectors.joining(", "));
        errorMessage += " must not be missing";
        redirectAttributes.addFlashAttribute("error", "Invalid request: " + errorMessage);
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public String tasks(Model model,
            @RequestParam(required = false) @ValidEnum(enumClass = Status.class) String status,
            @RequestParam(required = false) boolean sorted) {
        Iterable<Task> tasks;

        if (status != null) {

            if (sorted) {
                tasks = taskService.getTasksByStatusAndPriorityDesc(Status.valueOf(status));
            } else {
                tasks = taskService.getTasksByStatus(Status.valueOf(status));
            }
        } else {
            if (sorted) {
                tasks = taskService.getAllTasksSortedByPriorityDesc();
            } else {
                tasks = taskService.getAllTasks();
            }
        }
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public String addTask(Model model, RedirectAttributes redirectAttributes, @RequestParam @NotEmpty String title,
            @RequestParam @NotEmpty String description,
            @RequestParam @NotEmpty String dueDate,
            @RequestParam @NotNull @ValidEnum(enumClass = Priority.class) String priority) {

        Priority taskPriority = Priority.valueOf(priority);
        try {
            taskService.createTask(title, description, dueDate, taskPriority);

        } catch (InvalidDateException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format");
            return "redirect:/tasks";
        }
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.PUT)
    public String editTask(@RequestParam @NotNull Long id, @RequestParam @NotEmpty String title,
            @RequestParam @NotEmpty String description,
            @RequestParam @NotNull @ValidEnum(enumClass = Status.class) String status,
            @NotEmpty @RequestParam String dueDate,
            @RequestParam @NotNull @ValidEnum(enumClass = Priority.class) String priority,
            RedirectAttributes redirectAttributes) {

        try {
            taskService.updateTask(id, title, description, Status.valueOf(status), dueDate, Priority.valueOf(priority));
        } catch (InvalidDateException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format");
            return "redirect:/tasks";
        } catch (TaskNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Task not found");
            return "redirect:/tasks";
        }
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE)
    public String deleteTask(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
        } catch (TaskNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Task not found");
            return "redirect:/tasks";
        }
        return "redirect:/tasks";
    }
}