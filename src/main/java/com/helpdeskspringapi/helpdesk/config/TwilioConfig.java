package com.helpdeskspringapi.helpdesk.config;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
public class TwilioConfig {

        // Find your Account Sid and Token at console.twilio.com
        public static final String ACCOUNT_SID = "[Account SID]";
        public static final String AUTH_TOKEN = "[Auth Token]";

        public static void main(String[] args) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message message = Message
                    .creator(
                            new PhoneNumber("+5519991731543"),
                            new PhoneNumber("+18382594506"),
                            "This is the ship that made the Kessel Run in fourteen parsecs?"
                    )
                    .create();

            System.out.println(message.getSid());
        }
    }

