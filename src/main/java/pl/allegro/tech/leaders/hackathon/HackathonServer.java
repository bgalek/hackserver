package pl.allegro.tech.leaders.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HackathonServer {

    public static void main(String[] args) {
        SpringApplication.run(HackathonServer.class, args);
    }

}

