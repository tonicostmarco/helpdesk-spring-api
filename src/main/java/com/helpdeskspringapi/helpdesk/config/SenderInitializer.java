package com.helpdeskspringapi.helpdesk.config;

import com.twilio.Twilio;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class SenderInitializer {

    public Logger log = Logger.getLogger(SenderInitializer.class.getName());

    public SenderInitializer(TwilioConfig senderConfiguration) {

        Twilio.init(senderConfiguration.getAccountSid(), senderConfiguration.getAuthToken());

        log.info("Twilio initialized");

    }

}
