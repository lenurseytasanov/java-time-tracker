package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.TaskJpaRepository;
import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.domain.User;
import edu.spring.javatimetracker.util.exception.NotFoundException;
import edu.spring.javatimetracker.util.exception.ResourceExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskJpaRepository taskJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Spy
    private Clock clock = Clock.systemDefaultZone();

    @InjectMocks
    private TaskService taskService;

    private Task task1;

    private Task task2;

    @BeforeEach
    public void setUp() {
        task1 = new Task("task 1");
        task1.setId(1L);
        task2 = new Task("task 2");
        task2.setId(2L);
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Если задача существует - исключение</li>
     *     <li>Если нет - сохранить задачу в бд</li>
     *     <li>Запустить задачу</li>
     *     <li>Назначить ответственного на задачу</li>
     * </ul>
     */
    @Test
    public void createTaskTest() {
        when(taskJpaRepository.findByDescription("task 1")).thenReturn(Optional.of(task1));
        when(taskJpaRepository.findByDescription("task 2")).thenReturn(Optional.empty());

        User user = new User("username", "pass", "name", "name");
        when(userJpaRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Exception ex = assertThrows(ResourceExistsException.class, () -> taskService.createTask("username", "task 1"));
        assertEquals("Task 'task 1' already exists", ex.getMessage());

        Task task = taskService.createTask("username", "task 2");
        assertEquals("task 2", task.getDescription());
        assertNotNull(task.getStartedAt());
        assertEquals(user, task.getAssignee());
        assertEquals(Set.of(task), user.getTasks());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Если задача не существует - исключение</li>
     *     <li>Если существует - заполнить finishedAt</li>
     * </ul>
     */
    @Test
    public void finishTaskTest() {
        Task task = new Task("test",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                null);
        when(taskJpaRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskJpaRepository.findById(2L)).thenReturn(Optional.empty());

        taskService.finishTask(1L);
        assertNotNull(task.getFinishedAt());

        Exception ex = assertThrows(NotFoundException.class, () -> taskService.finishTask(2L));
        assertEquals("Task with id '2' not found", ex.getMessage());
    }
}
