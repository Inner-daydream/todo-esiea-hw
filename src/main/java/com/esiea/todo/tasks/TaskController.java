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

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException e,
            RedirectAttributes redirectAttributes) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString().replaceAll(".*\\.", ""))
                .collect(Collectors.joining(", "));
        errorMessage += " must not be empty";
        redirectAttributes.addFlashAttribute("error", "Invalid request: " + errorMessage);
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public String tasks(Model model) {
        Iterable<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public String addTask(Model model, RedirectAttributes redirectAttributes, @RequestParam @NotEmpty String title,
            @RequestParam @NotEmpty String description,
            @RequestParam @NotEmpty String dueDate) {
        try {
            taskService.createTask(title, description, dueDate);

        } catch (InvalidDateException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format");
            return "redirect:/tasks";
        }
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.PUT)
    public String editTask(@RequestParam @NotNull Long id, @RequestParam @NotEmpty String title,
            @RequestParam @NotEmpty String description,
            @RequestParam @NotNull Status status, @NotEmpty @RequestParam String dueDate,
            RedirectAttributes redirectAttributes) {

        try {
            taskService.updateTask(id, title, description, status, dueDate);

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