package com.helpdeskspringapi.helpdesk.controller.handlers;

import com.helpdeskspringapi.helpdesk.dtos.error.CustomError;
import com.helpdeskspringapi.helpdesk.dtos.error.ValidationError;
import com.helpdeskspringapi.helpdesk.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> dataBaseError(DatabaseException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<CustomError> invalidParameter(InvalidParameterException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> argumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ValidationError error = new ValidationError(Instant.now(), status.value(),"ERROR", request.getRequestURI());

        for (FieldError f : e.getFieldErrors()) {
            error.addError(f.getField(), f.getDefaultMessage());
        }
        for (ObjectError g : e.getBindingResult().getGlobalErrors()) {
            error.addError("global", g.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomError> forbidden(ForbiddenException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomError> business(BusinessException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<CustomError> message(BusinessException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        CustomError error = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

}
