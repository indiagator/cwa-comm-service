package com.egov.commservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Producer
{
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "communication";

    @Autowired //DEPENDENCY INJECTION PROMISE FULFILLED AT RUNTIME
    private KafkaTemplate<String, String> kafkaTemplate ;

    public void pubCommMessage(CommMessage commMessage) throws JsonProcessingException // LOGIN | REGISTER
    {
        // convert to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String datum =  objectMapper.writeValueAsString(commMessage);

       // logger.info(String.format("#### -> Producing message -> %s", datum));
        this.kafkaTemplate.send(TOPIC,"message-1", datum);
    }

}
