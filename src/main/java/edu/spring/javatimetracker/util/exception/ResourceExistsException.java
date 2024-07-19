package edu.spring.javatimetracker.util.exception;

public class ResourceExistsException extends RuntimeException {

    public ResourceExistsException() {
    }

    public ResourceExistsException(String message) {
        super(message);
    }
}
