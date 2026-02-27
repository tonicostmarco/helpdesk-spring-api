package com.helpdeskspringapi.helpdesk.dtos.error;

import io.swagger.v3.oas.annotations.media.Schema;

public class FieldMessage {

    @Schema(description = "Field name", example = "email")
    private String fieldName;

    @Schema(description = "Validation message", example = "Insert a valid e-mail")
    private String fieldMessage;

    public FieldMessage(String fieldName, String fieldMessage) {
        this.fieldName = fieldName;
        this.fieldMessage = fieldMessage;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldMessage() {
        return fieldMessage;
    }

    public void setFieldMessage(String fieldMessage) {
        this.fieldMessage = fieldMessage;
    }
}
