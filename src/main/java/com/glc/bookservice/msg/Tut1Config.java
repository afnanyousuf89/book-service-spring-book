package com.glc.bookservice.msg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.amqp.core.Queue;


@Profile({"msg","hello-world"})
@Configuration
public class Tut1Config {
    
    @Bean
    public Queue hello(){
        return new Queue("HELLO");    
    }
    @Profile("receiver")
    @Bean
    public Tut1Receiver receiver() {
        return new Tut1Receiver();
    }

    @Profile("sender")
    @Bean
    public Tut1Sender sender() {
        return new Tut1Sender();
    }


}
