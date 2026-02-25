package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.config.TwilioConfig;
import com.helpdeskspringapi.helpdesk.dtos.twillio.MessageRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioMessageSenderService implements MessageSender {

    @Autowired
    public final TwilioConfig config;

    public TwilioMessageSenderService(TwilioConfig config) {
        this.config = config;
    }


    @Override
    public void sendSms(MessageRequest request) {

        Message message = Message
                .creator(
                        new PhoneNumber("whatsapp:+55" + request.ddd() + request.phoneNumber()),
                        new PhoneNumber(config.getTrialNumber()),
                        request.message()
                )
                .create();

        System.out.println(message.getSid());
    }

}

