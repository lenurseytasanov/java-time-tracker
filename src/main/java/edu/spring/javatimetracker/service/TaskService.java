package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.TaskJpaRepository;
import edu.spring.javatimetracker.domain.Task;
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

    @Transactional
    public Task startTime(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.start();
        return task;
    }

    @Transactional
    public Task stopTime(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.finish();
        return task;
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

    public void clearUserTasks(String username) {
        taskRepository.deleteUserTasks(username);
    }
}
