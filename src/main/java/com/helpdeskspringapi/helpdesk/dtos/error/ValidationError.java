package com.helpdeskspringapi.helpdesk.dtos.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError {

    @Schema(description = "List of field errors")
    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String fieldMessage) {

        errors.add(new FieldMessage(fieldName, fieldMessage));

    }
}
