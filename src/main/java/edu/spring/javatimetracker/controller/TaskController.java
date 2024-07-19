package edu.spring.javatimetracker.controller;

import edu.spring.javatimetracker.controller.dto.TaskDto;
import edu.spring.javatimetracker.controller.dto.TimeIntervalDto;
import edu.spring.javatimetracker.service.TaskService;
import edu.spring.javatimetracker.util.validation.Username;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/{username}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> createTask(@Username @PathVariable(name = "username") String username,
                                           @RequestBody String description) {
        taskService.createTask(username, description);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{task-id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "task-id") Long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{task-id}/start")
    public ResponseEntity<Void> startTask(@NotNull @PathVariable(name = "task-id") Long taskId) {
        taskService.startTime(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{task-id}/stop")
    public ResponseEntity<Void> finishTask(@NotNull @PathVariable(name = "task-id") Long taskId) {
        taskService.stopTime(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private List<OffsetDateTime> getTimeBoundaries(LocalDate start, LocalDate end, TimeZone timeZone) {
        return List.of(
                start != null ? start.atStartOfDay(timeZone.toZoneId()).toOffsetDateTime() : OffsetDateTime.MIN,
                end != null ? end.atStartOfDay(timeZone.toZoneId()).plusDays(1).toOffsetDateTime() : OffsetDateTime.MAX
        );
    }

    @GetMapping("/list")
    public ResponseEntity<Iterable<TaskDto>> findUserTasks(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate start,
            @RequestParam(name = "to", required = false) LocalDate end,
            @NotNull TimeZone timeZone) {
        List<OffsetDateTime> boundaries = getTimeBoundaries(start, end, timeZone);
        Iterable<TaskDto> response = taskService.findUserTasks(username, boundaries.getFirst(), boundaries.getLast()).stream()
                .map(task -> new TaskDto(
                        task.getDescription(), Duration.between(task.getStartedAt(), task.getFinishedAt())))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/work-intervals")
    public ResponseEntity<Iterable<TimeIntervalDto>> findUserIntervals(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate start,
            @RequestParam(name = "to", required = false) LocalDate end,
            @NotNull TimeZone timeZone) {
        List<OffsetDateTime> boundaries = getTimeBoundaries(start, end, timeZone);
        Iterable<TimeIntervalDto> response = taskService.findUserIntervals(username, boundaries.getFirst(), boundaries.getLast()).stream()
                .map(task -> new TimeIntervalDto(task.getStartedAt(), task.getFinishedAt(), task.getDescription()))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/work-time")
    public ResponseEntity<Duration> findUserWorkTime(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate start,
            @RequestParam(name = "to", required = false) LocalDate end,
            @NotNull TimeZone timeZone) {
        List<OffsetDateTime> boundaries = getTimeBoundaries(start, end, timeZone);
        Duration response = taskService.findUserWorkTime(username, boundaries.getFirst(), boundaries.getLast());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserTasks(@Username @PathVariable(name = "username") String username) {
        taskService.clearUserTasks(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
