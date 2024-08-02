package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.TaskJpaRepository;
import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.domain.User;
import edu.spring.javatimetracker.util.exception.NotFoundException;
import edu.spring.javatimetracker.util.exception.ResourceExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskJpaRepository taskRepository;

    private final UserJpaRepository userRepository;

    private final Clock clock;

    public static final OffsetDateTime LOWER_TIME_BOUNDARY = OffsetDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    
    public static final OffsetDateTime UPPER_TIME_BOUNDARY = OffsetDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    private static final String TASK_NOT_FOUND = "Task with id '%d' not found";

    private static final String USER_NOT_FOUND = "User '%s' not found";

    /**
     * Создает задачу для указанного пользователя; запускает таймер для задачи.
     * Задача сохраняется автоматически благодаря каскадированию у объекта User, а также тому,
     * что метод выполняется в рамках транзакции.
     * @param username имя пользователя
     * @param description описание задачи
     * @return созданная задача
     * @throws ResourceExistsException задача с заданным описанием уже существует
     * @throws NotFoundException пользователь не найден
     */
    @Transactional
    public Task createTask(String username, String description) throws ResourceExistsException, NotFoundException {
        taskRepository.findByDescription(description).ifPresent(task -> {
            throw new ResourceExistsException("Task '%s' already exists".formatted(description));
        });
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.formatted(username)));
        Task task = new Task(description);
        user.addTask(task);
        task.start(clock);
        log.info("User '{}' create a task '{}' with id '{}'", username, task.getDescription(), task.getId());
        return task;
    }

    @Transactional
    public void finishTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new NotFoundException(TASK_NOT_FOUND.formatted(taskId)));
        task.finish(clock);
        log.info("Task '{}' with id '{}' finished", task.getDescription(), task.getId());
    }
    
    private OffsetDateTime convertStartDate(LocalDate date) {
        return date != null ? date.atStartOfDay(clock.getZone()).toOffsetDateTime() : LOWER_TIME_BOUNDARY;
    }
    
    private OffsetDateTime convertEndDate(LocalDate date) {
        return date != null ? date.atStartOfDay(clock.getZone()).plusDays(1).toOffsetDateTime() : UPPER_TIME_BOUNDARY;
    }
    
    public List<Task> findUserTasks(String username, LocalDate from, LocalDate to) {
        OffsetDateTime leftBound = convertStartDate(from);
        OffsetDateTime rightBound = convertEndDate(to);
        return taskRepository.findUserTasks(username, leftBound, rightBound);
    }

    public List<Task> findUserIntervals(String username, LocalDate from, LocalDate to) {
        OffsetDateTime leftBound = convertStartDate(from);
        OffsetDateTime rightBound = convertEndDate(to);
        return taskRepository.findUserIntervals(username, leftBound, rightBound);
    }

    public Duration findUserWorkTime(String username, LocalDate from, LocalDate to) {
        OffsetDateTime leftBound = convertStartDate(from);
        OffsetDateTime rightBound = convertEndDate(to);
        return Duration.ofNanos(taskRepository.getUserTimeSum(username, leftBound, rightBound));
    }

    @Transactional
    public void clearUserTasks(String username) {
        taskRepository.deleteUserTasks(username);
        log.info("User '{}' delete all his finished tasks", username);
    }

    @Transactional
    public void finishAllTasks() {
        taskRepository.findAll().stream()
                .filter(task -> !task.isFinished())
                .forEach(task -> task.finish(clock));
        log.info("All tasks finished");
    }

    @Transactional
    public void deleteOldTasks(Duration taskTtl) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        taskRepository.findAll().stream()
                .filter(task -> task.isFinished() && task.getFinishedAt().plus(taskTtl).isBefore(now))
                .forEach(taskRepository::delete);
        log.info("Deleted all tasks older than {}", taskTtl);
    }
}
