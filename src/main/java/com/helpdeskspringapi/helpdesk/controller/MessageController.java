package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.twillio.MessageRequest;
import com.helpdeskspringapi.helpdesk.services.MessageSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {


    private final MessageSender sender;

    public MessageController(MessageSender sender) {
        this.sender = sender;
    }

    @Operation(
            description = "Message sender",
            summary = "Send custom message to an user by using Twilio",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPPORT', 'NOC', 'ADMIN')")
    @PostMapping("/send-message")
    public ResponseEntity<Void> sender(@RequestBody MessageRequest request) {
        sender.sendSms(request);

        return ResponseEntity.ok().build();

    }

}
