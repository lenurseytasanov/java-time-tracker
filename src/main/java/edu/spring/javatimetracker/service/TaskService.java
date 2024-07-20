package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.TaskJpaRepository;
import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.domain.User;
import edu.spring.javatimetracker.util.exception.NotFoundException;
import edu.spring.javatimetracker.util.exception.ResourceExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskJpaRepository taskRepository;

    private final UserJpaRepository userRepository;

    private static final String TASK_NOT_FOUND = "Task with id '%d' not found";

    private static final String USER_NOT_FOUND = "User '%s' not found";

    @Transactional
    public Task createTask(String username, String description) {
        taskRepository.findByDescription(description).ifPresent(task -> {
            throw new ResourceExistsException("Task '%s' already exists".formatted(description));
        });
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.formatted(username)));
        Task task = new Task(description);
        task.start();
        user.addTask(task);
        return taskRepository.findByDescription(description).orElseThrow();
    }

    @Transactional
    public void finishTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new NotFoundException(TASK_NOT_FOUND.formatted(taskId)));
        task.finish();
    }

    public List<Task> findUserTasks(String username, OffsetDateTime from, OffsetDateTime to) {
        return taskRepository.findUserTasks(username, from, to);
    }

    public List<Task> findUserIntervals(String username, OffsetDateTime from, OffsetDateTime to) {
        return taskRepository.findUserIntervals(username, from, to);
    }

    public Duration findUserWorkTime(String username, OffsetDateTime from, OffsetDateTime to) {
        return Duration.ofNanos(taskRepository.getUserTimeSum(username, from, to));
    }

    @Transactional
    public void clearUserTasks(String username) {
        taskRepository.deleteUserTasks(username);
    }
}
