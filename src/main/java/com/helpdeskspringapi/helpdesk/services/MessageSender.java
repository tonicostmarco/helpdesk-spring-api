package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.twillio.MessageRequest;

public interface MessageSender {

        void sendSms(MessageRequest request);


}
