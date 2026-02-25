package com.helpdeskspringapi.helpdesk.exceptions;

public class MessageException extends RuntimeException {
    public MessageException(String message) {
        super(message);
    }
}
