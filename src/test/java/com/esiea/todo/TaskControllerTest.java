package com.esiea.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.esiea.todo.tasks.InvalidDateException;
import com.esiea.todo.tasks.Status;
import com.esiea.todo.tasks.Task;
import com.esiea.todo.tasks.TaskService;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

        @MockBean
        private TaskService taskService;

        @Autowired
        private WebApplicationContext webApplicationContext;

        private MockMvc mockMvc;

        @BeforeEach
        public void setup() {
                // Initialize MockMvc with the full Spring application context
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Test
        public void testGetTasks() throws Exception {
                Task task = new Task("title", "description", new Date());
                Iterable<Task> tasks = Collections.singletonList(task);
                when(taskService.getAllTasks()).thenReturn(tasks);

                mockMvc.perform(get("/tasks"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("tasks"))
                                .andExpect(model().attributeExists("tasks"))
                                .andExpect(model().attribute("tasks", tasks));
        }

        @Test
        public void testAddTask() throws Exception {
                String title = "title";
                String description = "description";
                String dueDate = "2022-12-31";
                Task task = new Task(title, description, new Date());
                when(taskService.createTask(anyString(), anyString(), anyString())).thenReturn(task);

                mockMvc.perform(post("/tasks")
                                .param("title", title)
                                .param("description", description)
                                .param("dueDate", dueDate))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"));

                verify(taskService, times(1)).createTask(eq(title), eq(description),
                                eq(dueDate));
        }

        @Test
        public void testEditTask() throws Exception {
                Long id = 1L;
                String title = "title";
                String description = "description";
                String dueDate = "2022-12-31";
                Task task = new Task(title, description, new Date());
                when(taskService.updateTask(any(Long.class), anyString(), anyString(), any(Status.class), anyString()))
                                .thenReturn(task);

                mockMvc.perform(put("/tasks")
                                .param("id", id.toString())
                                .param("title", title)
                                .param("description", description)
                                .param("status", "IN_PROGRESS")
                                .param("dueDate", dueDate))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"));

                verify(taskService, times(1)).updateTask(eq(id), eq(title), eq(description), eq(Status.IN_PROGRESS),
                                eq(dueDate));
        }

        @Test
        public void testDeleteTask() throws Exception {
                Long id = 1L;
                mockMvc.perform(delete("/tasks/" + id))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"));

                verify(taskService, times(1)).deleteTask(eq(id));
        }

        @Test
        public void testCreateErrorInvalidDate() throws Exception {
                String title = "title";
                String description = "description";
                String dueDate = "2022/000-31";
                when(taskService.createTask(anyString(), anyString(), anyString()))
                                .thenThrow(new InvalidDateException(dueDate));

                mockMvc.perform(post("/tasks")
                                .param("title", title)
                                .param("description", description)
                                .param("dueDate", dueDate))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));
                ;
        }

        @Test
        void testEditInvalidDateFormat() throws Exception {
                Long id = 1L;
                String title = "title";
                String description = "description";
                String dueDate = "2022/000-31";
                when(taskService.updateTask(any(Long.class), anyString(), anyString(), any(Status.class), anyString()))
                                .thenThrow(new InvalidDateException(dueDate));

                mockMvc.perform(put("/tasks")
                                .param("id", id.toString())
                                .param("title", title)
                                .param("description", description)
                                .param("status", Status.IN_PROGRESS.toString())
                                .param("dueDate", dueDate))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));
        }

        @Test
        void testEmptyOrInvalidParameters() throws Exception {
                mockMvc.perform(post("/tasks")
                                .param("title", "")
                                .param("description", "zz")
                                .param("dueDate", "2020-12-31"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));

                mockMvc.perform(put("/tasks")
                                .param("id", "1")
                                .param("title", "ee")
                                .param("description", "aa")
                                .param("status", "")
                                .param("dueDate", "2020-12-31"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));

                mockMvc.perform(put("/tasks")
                                .param("id", "1")
                                .param("title", "ee")
                                .param("description", "aa")
                                .param("status", "ee")
                                .param("dueDate", ""))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));

                mockMvc.perform(put("/tasks")
                                .param("id", "1")
                                .param("title", "ee")
                                .param("description", "")
                                .param("status", "ee")
                                .param("dueDate", "2020-12-31"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));

                mockMvc.perform(put("/tasks")
                                .param("id", "1")
                                .param("title", "Hello")
                                .param("description", "aa")
                                .param("status", "HANDS IN THE AIR")
                                .param("dueDate", "2020-12-31"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tasks"))
                                .andExpect(flash().attributeExists("error"));
        }

}