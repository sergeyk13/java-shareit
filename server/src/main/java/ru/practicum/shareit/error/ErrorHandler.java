package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.error.model.ValidationException;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDefaultValidation(final MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String fieldName = fieldError.getField();
        String errorMessage = fieldError.getDefaultMessage();
        log.error(e.getMessage());
        return new ErrorResponse(
                String.format("Ошибка поля %s: %s", fieldName, errorMessage));
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleConversionFailed(ConversionFailedException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Unknown state: " + e.getValue());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundEntity(final EntityNotFoundException e) {
        return new ErrorResponse(cleanErrorMessage(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final ConflictException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final AccessDeniedException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNullPointer(final NullPointerException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForeignKeyViolation(DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(cleanErrorMessage(e.getMessage()));
    }

    private String cleanErrorMessage(String errorMessage) {
        int lastDotIndex = errorMessage.lastIndexOf(".");
        if (lastDotIndex != -1) {
            errorMessage = "Not found " + errorMessage.substring(lastDotIndex + 1);
        }
        return errorMessage;
    }
}

