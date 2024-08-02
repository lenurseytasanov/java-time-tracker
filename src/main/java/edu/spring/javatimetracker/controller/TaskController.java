package edu.spring.javatimetracker.controller;

import edu.spring.javatimetracker.controller.dto.TaskCreatedDto;
import edu.spring.javatimetracker.controller.dto.TaskDto;
import edu.spring.javatimetracker.controller.dto.TimeIntervalDto;
import edu.spring.javatimetracker.controller.dto.TimeSumDto;
import edu.spring.javatimetracker.domain.Task;
import edu.spring.javatimetracker.service.TaskService;
import edu.spring.javatimetracker.util.validation.Username;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/{username}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/new")
    @Operation(summary = "Create new task for user")
    public ResponseEntity<TaskCreatedDto> createTask(
            @Parameter(description = "Username of user", schema = @Schema(type = "string", example = "username")) @Username @PathVariable(name = "username") String username,
            @Parameter(description = "Task's content", schema = @Schema(type = "string", example = "Do some job")) @NotBlank @RequestBody String description) {
        Task task = taskService.createTask(username, description);
        return new ResponseEntity<>(new TaskCreatedDto(task.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/{task-id}/stop")
    public ResponseEntity<Void> finishTask(@NotNull @PathVariable(name = "task-id") Long taskId) {
        taskService.finishTask(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String formatDuration(Duration duration) {
        long m = duration.getSeconds() / 60;
        return "%02d:%02d".formatted(m / 60, m % 60);
    }

    @GetMapping
    public ResponseEntity<Iterable<TaskDto>> findUserTasks(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {
        Iterable<TaskDto> response = taskService.findUserTasks(username, from, to).stream()
                .map(task -> new TaskDto(
                        task.getDescription(), formatDuration(Duration.between(task.getStartedAt(), task.getFinishedAt()))))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/work-intervals")
    public ResponseEntity<Iterable<TimeIntervalDto>> findUserIntervals(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {
        Iterable<TimeIntervalDto> response = taskService.findUserIntervals(username, from, to).stream()
                .map(task -> new TimeIntervalDto(task.getStartedAt(), task.getFinishedAt(), task.getDescription()))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/work-time")
    public ResponseEntity<TimeSumDto> findUserWorkTime(
            @Username @PathVariable(name = "username") String username,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {
        TimeSumDto response = new TimeSumDto(formatDuration(
                taskService.findUserWorkTime(username, from, to)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserTasks(@Username @PathVariable(name = "username") String username) {
        taskService.clearUserTasks(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
