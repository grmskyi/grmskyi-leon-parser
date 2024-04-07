package com.example.leonparser;

import com.example.leonparser.parser.LeonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
public class LeonParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeonParserApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CommandLineRunner run(LeonParser leonParser) {
        return args -> {
            leonParser.getBettingData();
        };
    }
}