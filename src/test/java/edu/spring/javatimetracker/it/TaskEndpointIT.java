package edu.spring.javatimetracker.it;

import edu.spring.javatimetracker.db.repository.TaskJpaRepository;
import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    @BeforeEach
    public void setUp() {
        userJpaRepository.deleteAll();
        taskJpaRepository.deleteAll();

        User user = new User("default", "default", "default", "default");
        user.addTask(new Task("default task 1",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                null));
        user.addTask(new Task("default task 2",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC)));

        userJpaRepository.save(user);
    }

    @Test
    public void createTaskTest() throws Exception {
        mockMvc.perform(post("/api/default/tasks/new")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("new task"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber());

        Optional<Task> task = taskJpaRepository.findByDescription("new task");
        assertTrue(task.isPresent());
        assertNotNull(task.get().getStartedAt());
    }

    @Test
    public void finishTaskTest() throws Exception {
        Task savedTask = taskJpaRepository.findByDescription("default task 1").orElseThrow();

        mockMvc.perform(post("/api/default/tasks/" + savedTask.getId() + "/stop"))
                .andExpect(status().isOk());

        Optional<Task> task = taskJpaRepository.findByDescription("default task 1");
        assertTrue(task.isPresent());
        assertNotNull(task.get().getStartedAt());
        assertNotNull(task.get().getFinishedAt());
    }

    @Test
    public void findUserTasksTest() throws Exception {
        mockMvc.perform(get("/api/default/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].description").value("default task 2"))
                .andExpect(jsonPath("$[0].duration").value("02:30"));
    }

    @Test
    public void findUserIntervals() throws Exception {
        mockMvc.perform(get("/api/default/tasks/work-intervals"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].taskDescription").value("default task 2"))
                .andExpect(jsonPath("$[0].startedAt").value("2000-01-01T12:00:00Z"))
                .andExpect(jsonPath("$[0].finishedAt").value("2000-01-01T14:30:00Z"));
    }

    @Test
    public void findUserWorkTime() throws Exception {
        mockMvc.perform(get("/api/default/tasks/work-time"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.duration").value("02:30"));
    }

    @Test
    public void deleteUserTasks() throws Exception {
        mockMvc.perform(delete("/api/default/tasks"))
                .andExpect(status().isOk());

        assertTrue(taskJpaRepository.findAll().isEmpty());
    }
}
