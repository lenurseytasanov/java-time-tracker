package edu.spring.javatimetracker.it.jpa;

import edu.spring.javatimetracker.db.repository.TaskJpaRepository;
import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TaskJpaRepositoryTest {

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    public void setUp() {
        taskJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        User user1 = new User("username1", "password", "test", "test");
        user1.addTask(new Task("test task 1",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC)));
        user1.addTask(new Task("test task 2",
                OffsetDateTime.of(2000, 1, 1, 16, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 1, 20, 0, 0, 0, ZoneOffset.UTC)));
        user1.addTask(new Task("test task 3",
                OffsetDateTime.of(2000, 1, 2, 16, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 2, 16, 30, 0, 0, ZoneOffset.UTC)));

        User user2 = new User("username2", "password", "test", "test");
        user2.addTask(new Task("test task 4",
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2000, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC)));

        userJpaRepository.save(user1);
        userJpaRepository.save(user2);
    }

    /**
     * Проверяется каскадирование - при обновлении пользователя, обновляются задачи
     */
    @Test
    public void saveTasksTest() {
        List<Task> allTasks = taskJpaRepository.findAll();
        assertEquals(4, allTasks.size());

        userJpaRepository.findByUsername("username2").ifPresent(user -> {
            user.addTask(
                    new Task("test task 5")
            );
        });

        allTasks = taskJpaRepository.findAll();
        assertEquals(5, allTasks.size());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Фильтрация задач по имени пользователя и вхождению во временной интервал</li>
     *     <li>Порядок задач по убыванию времени выполнения</li>
     * </ul>
     */
    @Test
    public void findUserTasksTest() {
        OffsetDateTime from = LocalDate.of(2000, 1, 1)
                .atStartOfDay(ZoneId.of("Z"))
                .toOffsetDateTime();
        OffsetDateTime to = LocalDate.of(2000, 1, 2)
                .atStartOfDay(ZoneId.of("Z"))
                .toOffsetDateTime();

        List<String> actual = taskJpaRepository.findUserTasks("username1", from, to).stream()
                .map(Task::getDescription)
                .toList();

        assertEquals(2, actual.size());
        assertIterableEquals(List.of("test task 2", "test task 1"), actual);
    }

    /**
     * Проверяется: не возвращать незавершенные задачи
     */
    @Test
    public void findNotFinishedTaskTest() {
        userJpaRepository.findByUsername("username2").ifPresent(user -> {
            Task task1 = new Task("not finished task");
            task1.start(Clock.systemUTC());
            user.addTask(task1);

            user.addTask(new Task("not started task"));
        });

        List<Task> actual = taskJpaRepository.findUserTasks("username2", OffsetDateTime.MIN, OffsetDateTime.MAX);

        assertEquals(1, actual.size());
        assertEquals("test task 4", actual.getFirst().getDescription());
    }

    /**
     * Проверяется: порядок, фильтрация
     */
    @Test
    public void findUserIntervals() {
        OffsetDateTime from = LocalDate.of(2000, 1, 1)
                .atStartOfDay(ZoneId.of("Z"))
                .toOffsetDateTime();
        OffsetDateTime to = LocalDate.of(2000, 1, 2)
                .atStartOfDay(ZoneId.of("Z"))
                .toOffsetDateTime();

        List<String> actual = taskJpaRepository.findUserIntervals("username1", from, to).stream()
                .map(Task::getDescription)
                .toList();

        assertEquals(2, actual.size());
        assertIterableEquals(List.of("test task 1", "test task 2"), actual);
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Фильтрация применяется</li>
     *     <li>Незавершенные интерваллы не используются</li>
     * </ul>
     */
    @Test
    public void getUserTimeSumTest() {
        userJpaRepository.findByUsername("username1").ifPresent(user -> {
            user.addTask(new Task("not started task",
                    OffsetDateTime.of(2000, 1, 1, 20, 0, 0, 0, ZoneOffset.UTC), null));
        });

        OffsetDateTime from = LocalDate.of(2000, 1, 1)
                .atStartOfDay(ZoneId.of("Z"))
                .toOffsetDateTime();
        OffsetDateTime to = LocalDate.of(2000, 1, 2)
                .atStartOfDay(ZoneId.of("Z"))
                .toOffsetDateTime();

        Duration actual = Duration.ofNanos(taskJpaRepository.getUserTimeSum("username1", from, to));

        assertEquals(Duration.ofHours(6), actual);
    }

    /**
     * Проверяется: задачи пользователя удалены
     */
    @Test
    public void deleteUserTasksTest() {
        assertEquals(3, taskJpaRepository
                .findUserTasks("username1", OffsetDateTime.MIN, OffsetDateTime.MAX).size());

        taskJpaRepository.deleteUserTasks("username1");

        assertTrue(taskJpaRepository
                .findUserTasks("username1", OffsetDateTime.MIN, OffsetDateTime.MAX).isEmpty());

        assertEquals(1, taskJpaRepository
                .findUserTasks("username2", OffsetDateTime.MIN, OffsetDateTime.MAX).size());
    }
}
