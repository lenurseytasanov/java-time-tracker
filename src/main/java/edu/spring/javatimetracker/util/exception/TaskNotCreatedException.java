package edu.spring.javatimetracker.util.exception;

public class TaskNotCreatedException extends RuntimeException {

    public TaskNotCreatedException() {
    }

    public TaskNotCreatedException(String message) {
        super(message);
    }
}
