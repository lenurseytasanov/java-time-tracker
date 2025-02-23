package edu.spring.javatimetracker.handler;

import edu.spring.javatimetracker.controller.dto.ValidationErrorDto;
import edu.spring.javatimetracker.util.exception.NotFoundException;
import edu.spring.javatimetracker.util.exception.ResourceExistsException;
import edu.spring.javatimetracker.util.exception.TaskNotCreatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorDto> handleValidationErrors(HandlerMethodValidationException ex) {
        String errors = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        String message = "Validation errors: \n" + errors;
        log.debug("Validation exception: ", ex);
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorDto(400, "Bad Request", message));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error("Resource not found exception: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleResourceExistsException(ResourceExistsException ex) {
        log.error("Resource conflict exception: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TaskNotCreatedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleTaskNotCreatedException(TaskNotCreatedException ex) {
        log.error("Task not create exception: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
