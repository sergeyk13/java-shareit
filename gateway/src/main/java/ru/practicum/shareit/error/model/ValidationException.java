package ru.practicum.shareit.error.model;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

