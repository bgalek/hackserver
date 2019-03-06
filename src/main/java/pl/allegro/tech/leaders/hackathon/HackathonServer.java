package pl.allegro.tech.leaders.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableMBeanExport
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class HackathonServer {

    public static void main(String[] args) {
        SpringApplication.run(HackathonServer.class, args);
    }

}

