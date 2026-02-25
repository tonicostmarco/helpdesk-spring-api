package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.twillio.MessageRequest;
import com.helpdeskspringapi.helpdesk.services.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private MessageSender sender;

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PostMapping("/send-message")
    public ResponseEntity<Void> sender(@RequestBody MessageRequest request) {
        sender.sendSms(request, request.message());

        return ResponseEntity.ok().build();

    }

}
