package com.egov.commservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.View;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("api/v1")
public class MainRestController
{
    private static final Logger logger = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    TokenService tokenService;

    @Autowired
    Producer producer;

    @Autowired
    ApplicationContext context;
    @Autowired
    private View error;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("publish/project/messages")
       public ResponseEntity<?> publishProjectMessages(@RequestHeader("Authorization") String token,
                                                       @RequestBody Project project) throws InterruptedException {

           Thread.sleep(10000);

           Principal principal =  tokenService.validateToken(token);
           if(principal.getState().equals("VALID"))
           {
               logger.info("Token validated successfully");
               // Token is valid, proceed with the update

               if(project.getOwnerPhone().equals(principal.getUsername())) // AUTHORIZATION OF REQUEST HAPPENS HERE
               {
                   logger.info("Request received to publish project messages for project: " + project);
                   // Logic to publish project messages
                   // 1. fetch a list of contractors from the auth-service

                   WebClient authGetUsersWebClient = (WebClient) context.getBean("authGetUsersWebClient");

                   authGetUsersWebClient.get()
                            .uri("/CONTRACTOR")
                            .retrieve()
                            .bodyToFlux(String.class)
                            .subscribe(
                                           response ->{

                                               List<String> phoneNumbers = new ArrayList<>();

                                               ObjectMapper mapper = new ObjectMapper();
                                               try {
                                                   phoneNumbers = mapper.readValue(response, List.class);
                                               } catch (JsonProcessingException e) {
                                                   throw new RuntimeException(e);
                                               }

                                               phoneNumbers.stream().forEach(phoneNumber -> {
                                                   CommMessage commMessage = new CommMessage();
                                                   commMessage.setSender("ADMIN");
                                                   commMessage.setReceiver(phoneNumber);
                                                   commMessage.setContext("PROJECT");
                                                   commMessage.setContextid(project.getId());
                                                   commMessage.setMessage("New project created with ID: " + project.getId() + " and Name: " + project.getName());

                                                   try {
                                                       producer.pubCommMessage(commMessage);
                                                   } catch (JsonProcessingException e) {
                                                       throw new RuntimeException(e);
                                                   }

                                                   logger.info("New Message Published to TOPIC: COMMUNICATION" + commMessage.toString());
                                               });
                                           },
                                           error ->{ logger.error("AN ERROR OCCURRED"+ error.getMessage());}
                                   );
                     // 2. create a CommMessage for each contractor
                     // 3. use the producer to send the messages to the Kafka topic



//                   String stage2Key = String.valueOf(new Random().nextInt(100000));
//                   redisTemplate.opsForValue().set(stage2Key, "STAGE 2 COMPLETE", 30); // Store in Redis for 30 seconds
                   return ResponseEntity.ok("Project messages published for project ID: " + project.getId());
               }
               else
               {
                   logger.info("Phone number does not match with the token");
                   return ResponseEntity.status(401).body("Unauthorized: Phone number does not match with the token");
               }
           }
           else
           {
               logger.info("Token not valid");
               return ResponseEntity.status(401).body("Unauthorized: Invalid Token");
           }
       }
}
