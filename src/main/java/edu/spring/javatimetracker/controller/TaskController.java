package edu.spring.javatimetracker.controller;

import edu.spring.javatimetracker.controller.dto.TaskDto;
import edu.spring.javatimetracker.controller.dto.TimeIntervalDto;
import edu.spring.javatimetracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<Void> createTask(@RequestBody String description) {
        taskService.createTask(description);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/tasks/{task-id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tasks/{task-id}")
    public ResponseEntity<Void> startTask(@PathVariable Long taskId) {
        taskService.startTime(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tasks/{task-id}")
    public ResponseEntity<Void> finishTask(@PathVariable Long taskId) {
        taskService.stopTime(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/{username}/tasks")
    public ResponseEntity<Iterable<TaskDto>> findUserTasks(
            @PathVariable String username,
            @RequestParam(name = "from") OffsetDateTime start, @RequestParam(name = "to") OffsetDateTime end) {
        Iterable<TaskDto> response = taskService.findUserTasks(username, start, end).stream()
                .map(task -> new TaskDto(
                        task.getDescription(), Duration.between(task.getStartedAt(), task.getFinishedAt())))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{username}/intervals")
    public ResponseEntity<Iterable<TimeIntervalDto>> findUserIntervals(
            @PathVariable String username,
            @RequestParam(name = "from") OffsetDateTime start, @RequestParam(name = "to") OffsetDateTime end) {
        Iterable<TimeIntervalDto> response = taskService.findUserIntervals(username, start, end).stream()
                .map(task -> new TimeIntervalDto(task.getStartedAt(), task.getFinishedAt(), task.getDescription()))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{username}/work-time")
    public ResponseEntity<Duration> findUserWorkTime(
            @PathVariable String username,
            @RequestParam(name = "from") OffsetDateTime start, @RequestParam(name = "to") OffsetDateTime end) {
        Duration response = taskService.findUserWorkTime(username, start, end);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/{username}/tasks")
    public ResponseEntity<Void> deleteUserTasks(@PathVariable String username) {
        taskService.clearUserTasks(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
