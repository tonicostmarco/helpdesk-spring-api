package com.helpdeskspringapi.helpdesk.dtos.twillio;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageRequest(String sender,
                             int ddd,
                             String phoneNumber,
                             String message) {

    public MessageRequest(@JsonProperty("sender") String sender,
                          @JsonProperty("ddd")int ddd,
                          @JsonProperty("phoneNumber")String phoneNumber,
                          @JsonProperty("message")String message) {
        this.sender = sender;
        this.ddd = ddd;
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

}
