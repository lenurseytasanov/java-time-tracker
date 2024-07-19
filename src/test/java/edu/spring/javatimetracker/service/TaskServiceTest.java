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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskJpaRepository taskJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

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

        taskService.createTask("username", "task 2");
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Задача удалена из бд</li>
     *     <li>Исключение, если задача с данным id не найдена</li>
     * </ul>
     */
    @Test
    public void deleteUserTest() {
        when(taskJpaRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskJpaRepository.findById(2L)).thenReturn(Optional.empty());

        taskService.deleteTask(1L);
        verify(taskJpaRepository).delete(task1);

        Exception ex = assertThrows(NotFoundException.class, () -> taskService.deleteTask(2L));
        assertEquals("Task with id '2' not found", ex.getMessage());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Здача запущена</li>
     *     <li>Исключение, если задача с данным id не найдена</li>
     * </ul>
     */
    @Test
    public void startTaskTest() {
        when(taskJpaRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskJpaRepository.findById(2L)).thenReturn(Optional.empty());

        taskService.startTime(1L);
        assertNotNull(task1.getStartedAt());

        Exception ex = assertThrows(NotFoundException.class, () -> taskService.startTime(2L));
        assertEquals("Task with id '2' not found", ex.getMessage());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Задача завершена</li>
     *     <li>Исключение, если задача с данным id не найдена</li>
     * </ul>
     */
    @Test
    public void stopTaskTest() {
        when(taskJpaRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskJpaRepository.findById(2L)).thenReturn(Optional.empty());

        taskService.startTime(1L);
        taskService.stopTime(1L);
        assertNotNull(task1.getFinishedAt());

        Exception ex = assertThrows(NotFoundException.class, () -> taskService.stopTime(2L));
        assertEquals("Task with id '2' not found", ex.getMessage());
    }
}
