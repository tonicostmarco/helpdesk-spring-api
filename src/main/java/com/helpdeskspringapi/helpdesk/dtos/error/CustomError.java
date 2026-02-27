package com.helpdeskspringapi.helpdesk.dtos.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public class CustomError {

    @Schema(description = "Error timestamp (UTC)", example = "2026-02-27T21:05:10Z", format = "date-time")
    private Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    @Schema(description = "Error title", example = "Bad Request")
    private String error;

    @Schema(description = "Request path", example = "/tickets/10")
    private String path;

    public CustomError(Instant timestamp, Integer status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }


    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
