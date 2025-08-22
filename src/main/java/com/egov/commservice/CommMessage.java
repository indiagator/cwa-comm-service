package com.egov.commservice;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommMessage {

    String sender; //USERNAME
    String receiver; //USERNAME
    String message; //PAYLOAD
    String context; //PROFILE, PROJECT, QUOTE, PAYMENT, MILESTONE, DISPUTE ...
    String contextid; //ID of the context object

    @Override
    public String toString() {
        return "CommMessage{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", context='" + context + '\'' +
                ", contextid='" + contextid + '\'' +
                '}';
    }
}
