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
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/{username}/tasks")
@RequiredArgsConstructor
public class TaskController {

    public static final OffsetDateTime LOWER_TIME_BOUNDARY = OffsetDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime UPPER_TIME_BOUNDARY = OffsetDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

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
                start != null ? start.atStartOfDay(timeZone.toZoneId()).toOffsetDateTime() : LOWER_TIME_BOUNDARY,
                end != null ? end.atStartOfDay(timeZone.toZoneId()).plusDays(1).toOffsetDateTime() : UPPER_TIME_BOUNDARY
        );
    }

    private String formatDuration(Duration duration) {
        long m = duration.getSeconds() / 60;
        return "%02d:%02d".formatted(m / 60, m);
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
                        task.getDescription(), formatDuration(Duration.between(task.getStartedAt(), task.getFinishedAt()))))
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
    public ResponseEntity<String> findUserWorkTime(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate start,
            @RequestParam(name = "to", required = false) LocalDate end,
            @NotNull TimeZone timeZone) {
        List<OffsetDateTime> boundaries = getTimeBoundaries(start, end, timeZone);
        String response = formatDuration(
                taskService.findUserWorkTime(username, boundaries.getFirst(), boundaries.getLast()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserTasks(@Username @PathVariable(name = "username") String username) {
        taskService.clearUserTasks(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
