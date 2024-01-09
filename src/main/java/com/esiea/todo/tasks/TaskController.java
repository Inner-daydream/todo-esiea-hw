package com.esiea.todo.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TaskController {

    @Autowired
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public String tasks(Model model) {
        Iterable<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public String addTask(Model model, RedirectAttributes redirectAttributes, @RequestParam String title,
            @RequestParam String description,
            @RequestParam String dueDate) {
        try {
            taskService.createTask(title, description, dueDate);

        } catch (InvalidDateException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format");
            return "redirect:/tasks";
        }
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.PUT)
    public String editTask(@RequestParam Long id, @RequestParam String title, @RequestParam String description,
            @RequestParam Status status, @RequestParam String dueDate, RedirectAttributes redirectAttributes) {

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