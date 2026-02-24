package com.helpdeskspringapi.helpdesk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

     private String accountSid;
     private String authToken;
     private String trialNumber;

    public TwilioConfig(@Value("${twilio.account-sid}")String accountSid,
                        @Value("${twilio.auth-token}")String authToken,
                        @Value("${twilio.whatsapp-from}")String trialNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.trialNumber = trialNumber;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getTrialNumber() {
        return trialNumber;
    }

    public void setTrialNumber(String trialNumber) {
        this.trialNumber = trialNumber;
    }
}


