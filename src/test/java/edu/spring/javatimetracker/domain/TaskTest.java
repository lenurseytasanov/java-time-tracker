package edu.spring.javatimetracker.domain;

import edu.spring.javatimetracker.util.exception.TaskNotCreatedException;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaskTest {

    private final Clock clock = Clock.fixed(
            LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("Z"));

    /**
     * Проверяется:
     * <ul>
     *     <li>Задача уже запущена или завершена - исключение</li>
     *     <li>Иначе - запустить таймер в установленное время</li>
     * </ul>
     */
    @Test
    public void startTaskTest() {
        Task task1 = new Task("test 1",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                null);
        Task task2 = new Task("test 2",
                null,
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        Task task3 = new Task("test 3");

        Exception ex1 = assertThrows(TaskNotCreatedException.class, () -> task1.start(clock));
        assertEquals("Task 'test 1' already started", ex1.getMessage());
        Exception ex2 = assertThrows(TaskNotCreatedException.class, () -> task2.start(clock));
        assertEquals("Task 'test 2' already started", ex2.getMessage());

        task3.start(clock);
        assertEquals(clock.instant(), task3.getStartedAt().toInstant());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Задача не запущена или завершена - исключение</li>
     *     <li>Иначе - остановить таймер в установленное время</li>
     * </ul>
     */
    @Test
    public void finishTaskTest() {
        Task task1 = new Task("test 1",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                null);
        Task task2 = new Task("test 2",
                null,
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        Task task3 = new Task("test 3");

        task1.finish(clock);
        assertEquals(clock.instant(), task1.getFinishedAt().toInstant());

        Exception ex1 = assertThrows(TaskNotCreatedException.class, () -> task2.finish(clock));
        assertEquals("Task 'test 2' already finished or not started", ex1.getMessage());
        Exception ex2 = assertThrows(TaskNotCreatedException.class, () -> task3.finish(clock));
        assertEquals("Task 'test 3' already finished or not started", ex2.getMessage());
    }
}
