package com.esiea.todo.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public String addTask(Model model, RedirectAttributes redirectAttributes, @RequestParam String title,
            @RequestParam String description,
            @RequestParam String dueDate) {
        Date parsedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            parsedDate = format.parse(dueDate);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format");
            return "redirect:/tasks";
        }
        System.out.println("dueDate: " + dueDate);
        taskService.createTask(title, description, parsedDate);
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.PUT)
    public String editTask(@RequestParam Long id, @RequestParam String title, @RequestParam String description,
            @RequestParam Status status, @RequestParam String dueDate, RedirectAttributes redirectAttributes) {
        Date parsedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            parsedDate = format.parse(dueDate);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format");
            return "redirect:/tasks";
        }
        try {
            taskService.updateTask(id, title, description, status, parsedDate);
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