package edu.spring.javatimetracker.it.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.spring.javatimetracker.controller.TaskController;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    public void whenCorrectRequest_createTaskAndReturnId() throws Exception {
        Task task = new Task("new task");
        task.setId(1L);
        when(taskService.createTask("default", task.getDescription())).thenReturn(task);

        mockMvc.perform(post("/api/default/tasks/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task.getDescription()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()));
    }

    @Test
    public void createTask_whenValidationErrors_returnBadRequest() throws Exception {
        String username = "a".repeat(1000);

        mockMvc.perform(post("/api/" + username + "/tasks/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(" "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Validation errors: \nsize must be between 0 and 255\nmust not be blank"));
    }

    @Test
    public void whenCorrectRequest_finishTask() throws Exception {
        mockMvc.perform(post("/api/default/tasks/1/stop"))
                .andExpect(status().isOk());

        verify(taskService).finishTask(1L);
    }

    @Test
    public void whenCorrectRequest_returnUserTasks() throws Exception {
        Task task = new Task("new task",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC));
        task.setId(1L);
        when(taskService.findUserTasks(eq("default"), any(), any())).thenReturn(List.of(task));

        mockMvc.perform(get("/api/default/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].description").value("new task"))
                .andExpect(jsonPath("$[0].duration").value("02:00"));

        mockMvc.perform(get("/api/default/tasks?from=2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].description").value("new task"))
                .andExpect(jsonPath("$[0].duration").value("02:00"));

        mockMvc.perform(get("/api/default/tasks?to=2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].description").value("new task"))
                .andExpect(jsonPath("$[0].duration").value("02:00"));
    }

    @Test
    public void findUserTasks_whenValidationErrors_returnBadRequest() throws Exception {
        mockMvc.perform(get("/api/ /tasks"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Validation errors: \nmust not be blank"));

    }

    @Test
    public void whenCorrectRequest_returnUserIntervals() throws Exception {
        Task task = new Task("new task",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC));
        task.setId(1L);
        when(taskService.findUserIntervals(eq("default"), any(), any())).thenReturn(List.of(task));

        mockMvc.perform(get("/api/default/tasks/work-intervals"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].taskDescription").value("new task"))
                .andExpect(jsonPath("$[0].startedAt").value("2000-01-01T12:00:00Z"))
                .andExpect(jsonPath("$[0].finishedAt").value("2000-01-01T14:00:00Z"));

        mockMvc.perform(get("/api/default/tasks/work-intervals?from=2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].taskDescription").value("new task"))
                .andExpect(jsonPath("$[0].startedAt").value("2000-01-01T12:00:00Z"))
                .andExpect(jsonPath("$[0].finishedAt").value("2000-01-01T14:00:00Z"));

        mockMvc.perform(get("/api/default/tasks/work-intervals?to=2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].taskDescription").value("new task"))
                .andExpect(jsonPath("$[0].startedAt").value("2000-01-01T12:00:00Z"))
                .andExpect(jsonPath("$[0].finishedAt").value("2000-01-01T14:00:00Z"));
    }

    @Test
    public void findUserIntervals_whenValidationErrors_returnBadRequest() throws Exception {
        mockMvc.perform(get("/api/ /tasks/work-intervals"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Validation errors: \nmust not be blank"));

    }

    @Test
    public void whenCorrectRequest_returnUserWorkTime() throws Exception {
        when(taskService.findUserWorkTime(eq("default"), any(), any())).thenReturn(Duration.ofHours(2));

        mockMvc.perform(get("/api/default/tasks/work-time"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.duration").value("02:00"));

        mockMvc.perform(get("/api/default/tasks/work-time?from=2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.duration").value("02:00"));

        mockMvc.perform(get("/api/default/tasks/work-time?to=2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.duration").value("02:00"));
    }

    @Test
    public void findUserWorkTime_whenValidationErrors_returnBadRequest() throws Exception {
        mockMvc.perform(get("/api/ /tasks/work-time"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Validation errors: \nmust not be blank"));

    }

    @Test
    public void whenCorrectRequest_deleteUserTasks() throws Exception {
        mockMvc.perform(delete("/api/default/tasks"));
        verify(taskService).clearUserTasks("default");
    }

    @Test
    public void deleteUserTasks_whenValidationErrors_returnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/ /tasks"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Validation errors: \nmust not be blank"));

    }
}
