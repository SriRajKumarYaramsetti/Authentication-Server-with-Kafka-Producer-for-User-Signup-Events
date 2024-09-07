package com.SriRaj.UserService.clients;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerClient {
        private KafkaTemplate<String,String> kafkaTemplate;

        public KafkaProducerClient(KafkaTemplate<String,String> kafkaTemplate){
            this.kafkaTemplate=kafkaTemplate;
        }

        public void sendMessage(String topic,String message){
            kafkaTemplate.send(topic,message);
        }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
